# Diagramas de secuencia

Estos diagramas muestran qué pasa, en orden, desde que el usuario hace clic en el front hasta que el dato queda guardado en SQL Server. Cada participante tiene el nombre de la clase real, para poder abrir el código mientras se lee, y está dentro de la caja de su capa:

| Caja | Qué hay adentro |
|---|---|
| **Api** (gris) | Controladores, filtro JWT y `GlobalExceptionHandler`: reciben HTTP y responden HTTP. |
| **Application** (azul) | Servicios y mappers: coordinan el caso de uso. También los puertos que no son del negocio (`IRabbitMQSender`, `IPasswordHasher`, `ITokenProvider`). |
| **Domain** (amarillo) | Entidades, objetos de estado (patrón State), puertos de los repositorios y excepciones del dominio. **Aquí están las reglas del negocio.** |
| **Infrastructure** (gris) | Adaptadores: repositorios JPA, publicador de RabbitMQ, BCrypt y JWT. |

SQL Server, RabbitMQ y el front quedan afuera de las cajas: son otros procesos.

Al leerlos, fíjate en dos cosas:

- **La caja Domain no llama a nadie que esté fuera de ella.** El servicio usa el puerto (`IConvocationRepository`, que es del dominio), y la implementación de Infrastructure se conecta por inyección de dependencias. Por eso las flechas hacia la base de datos salen del puerto con el texto *implementado por*.
- **Las reglas las decide el dominio y el error lo traduce la capa Api.** El dominio lanza `DomainValidationException` (o sus hijas `InvalidConvocationStateException` e `InvalidRequestStateException`) sin saber que existe HTTP, y `GlobalExceptionHandler` la convierte en un 400.

1. [Crear una convocatoria](#1-crear-una-convocatoria-cu-001) (CU-001)
2. [Cambiar el estado de una convocatoria](#2-cambiar-el-estado-de-una-convocatoria-patrón-state) (patrón State)
3. [Crear una solicitud](#3-crear-una-solicitud-cu-002) (CU-002)
4. [Aprobar o rechazar una solicitud](#4-aprobar-o-rechazar-una-solicitud) (patrón State y eventos)
5. [Retirar una solicitud](#5-retirar-una-solicitud)
6. [Iniciar sesión y usar el token](#6-iniciar-sesión-y-usar-el-token)

Para no repetir, los diagramas 1 a 5 suponen que la petición ya pasó la seguridad (el token es válido y el rol puede usar el endpoint). Cómo se revisa eso está en el [diagrama 6](#6-iniciar-sesión-y-usar-el-token).

## 1. Crear una convocatoria (CU-001)

El administrador llena el formulario y lo envía. Hay dos niveles de validación: el **formato** de los campos lo revisa Bean Validation (`@Valid`) en la capa Api antes de entrar al servicio, y las **reglas del negocio** (fechas y requisitos) las revisa la entidad `Convocation`, que a su vez le pide a cada `ConvocationRequirement` que se valide.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    participant Front as Front React
    box rgba(229,231,235,0.6) Api
        participant Ctrl as ConvocationController
        participant Err as GlobalExceptionHandler
    end
    box rgba(219,234,254,0.6) Application
        participant Svc as ConvocationService
        participant Map as ConvocationMapper
    end
    box rgba(255,243,196,0.8) Domain
        participant Conv as Convocation
        participant CReq as ConvocationRequirement
        participant Port as IRequirementRepository<br/>IConvocationRepository<br/>IConvocationRequirementRepository<br/>(puertos)
    end
    box rgba(229,231,235,0.6) Infrastructure
        participant Repo as RequirementRepository<br/>ConvocationRepository...<br/>(adaptadores JPA)
    end
    participant DB as SQL Server

    Admin->>Front: Llena el formulario y confirma
    Front->>Ctrl: POST /convocation/createConvocation
    Note over Ctrl: @Valid revisa campos obligatorios y formatos
    Ctrl->>Svc: createConvocation(ConvocationDto)
    Svc->>Map: toDomain(dto)
    Map-->>Svc: Convocation
    Svc->>Port: requirementRepository.findAll()
    Port->>Repo: implementado por
    Repo->>DB: SELECT Requirement
    Repo-->>Svc: catálogo de Requirement
    Svc->>Conv: validate(hoy, catálogo)
    Note over Conv: inicio después de hoy<br/>fin al menos un día después del inicio
    loop cada requisito
        Conv->>CReq: validate(catálogo, campo, errores)
        Note over CReq: el tipo existe, el valor es mayor a 0<br/>y una nota no pasa de 5.0
    end
    alt alguna regla no se cumple
        Conv-->>Svc: DomainValidationException (campo y mensaje)
        Svc-->>Err: la excepción sube sin capturarse
        Err-->>Front: 400 { data: { startDate: "La fecha de inicio debe ser posterior a la fecha actual" } }
        Front-->>Admin: el error debajo de cada campo
    else todo es válido
        Svc->>Conv: setState(OPEN)
        Svc->>Port: convocationRepository.save(convocation)
        Port->>Repo: implementado por
        Note over Repo: ConvocationPersistenceMapper<br/>la convierte en ConvocationEntity
        Repo->>DB: INSERT Convocation
        Repo-->>Svc: Convocation con id
        Svc->>Port: convocationRequirementRepository.saveAll(requisitos)
        Port->>Repo: implementado por
        Repo->>DB: INSERT ConvocationRequirement
        Svc->>Map: toDto(convocation)
        Svc-->>Ctrl: ConvocationDto
        Ctrl-->>Front: 200 "New convocation"
        Front-->>Admin: vuelve a la lista con la convocatoria
    end
```

**Qué mostrar de aquí:** el controlador no tiene ninguna regla, solo recibe y responde. El servicio no sabe validar fechas, se lo pide a la entidad. Y como `validate` corre antes del primer `INSERT`, una convocatoria inválida nunca llega a la base; si fallara el segundo `INSERT`, `@Transactional` deshace también el primero.

## 2. Cambiar el estado de una convocatoria (patrón State)

Ejemplo con *cerrar*. Las cuatro acciones (cerrar, reabrir, finalizar y cancelar) pasan por el mismo método privado `changeState` del servicio: buscar, dejar que el estado aplique la transición y guardar.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    box rgba(229,231,235,0.6) Api
        participant Ctrl as ConvocationController
        participant Err as GlobalExceptionHandler
    end
    box rgba(219,234,254,0.6) Application
        participant Svc as ConvocationService
    end
    box rgba(255,243,196,0.8) Domain
        participant Conv as Convocation
        participant States as ConvocationStates<br/>(fábrica)
        participant St as Estado actual<br/>OpenState, ClosedState...
        participant Port as IConvocationRepository<br/>(puerto)
    end
    box rgba(229,231,235,0.6) Infrastructure
        participant Repo as ConvocationRepository<br/>(adaptador JPA)
    end
    participant DB as SQL Server

    Admin->>Ctrl: PUT /convocation/closeConvocation/5
    Ctrl->>Svc: closeConvocation(5)
    Svc->>Port: findById(5)
    Port->>Repo: implementado por
    Repo->>DB: SELECT Convocation
    alt no existe
        Repo-->>Svc: Optional vacío
        Svc-->>Err: NotFoundException (de Application)
        Err-->>Admin: 404 "No existe una convocatoria con id 5"
    else existe
        Repo-->>Svc: Convocation
        Svc->>Conv: applyDateRules(hoy)
        Conv->>States: of(state)
        States-->>Conv: el objeto del estado actual
        Conv->>St: applyDateRules(this, hoy)
        Note over St: OpenState: si ya pasó la fecha de fin<br/>la convocatoria queda CLOSED
        Svc->>Conv: close()
        Conv->>States: of(state)
        Conv->>St: close(this)
        alt el estado permite cerrar (OpenState)
            St->>Conv: setState(CLOSED)
            Svc->>Port: save(convocation)
            Port->>Repo: implementado por
            Repo->>DB: UPDATE Convocation
            Svc-->>Ctrl: ConvocationDto con estado CLOSED
            Ctrl-->>Admin: 200 "Convocatoria cerrada"
        else no lo permite (Closed, Finished, Cancelled)
            St-->>Svc: InvalidConvocationStateException
            Svc-->>Err: la excepción sube
            Err-->>Admin: 400 { state: "No se puede cerrar una convocatoria cerrada" }
        end
    end
```

Las otras tres acciones siguen el mismo camino; solo cambia qué revisa el estado:

| Acción | Endpoint | Qué decide el dominio | Error típico |
|---|---|---|---|
| Reabrir | `PUT /convocation/reopenConvocation/{id}` | Solo `ClosedState` la permite, con una nueva fecha de fin posterior a hoy. | *"La nueva fecha de fin debe ser posterior a la fecha actual"* |
| Finalizar | `PUT /convocation/finishConvocation/{id}` | Solo `ClosedState`, y sin solicitudes por decidir. El servicio pregunta antes `existsByConvocationIdAndStateIn(id, UNDECIDED)` y le pasa el resultado al dominio. | *"No se puede finalizar: hay solicitudes sin aprobar ni rechazar"* |
| Cancelar | `PUT /convocation/cancelConvocation/{id}` | `OpenState` y `ClosedState`, con justificación obligatoria. | *"La justificación de la cancelación es obligatoria"* |

**Qué mostrar de aquí:** en `ConvocationService` no hay ningún `if (estado == ...)`. El servicio carga, delega y guarda. Quien decide si la acción es válida es el objeto del estado actual, que entrega `ConvocationStates.of(...)` (una fábrica simple). Si mañana se agrega un estado, se crea su clase sin tocar el servicio.

## 3. Crear una solicitud (CU-002)

El estudiante se postula con un valor para cada requisito. Las comprobaciones van de la más barata a la más costosa: que existan el estudiante y la convocatoria, que la convocatoria esté recibiendo solicitudes, que no haya una solicitud repetida y, al final, que las respuestas estén completas. El estudiante sale del token, no del cuerpo de la petición.

```mermaid
sequenceDiagram
    autonumber
    actor Est as Estudiante
    box rgba(229,231,235,0.6) Api
        participant Ctrl as RequestController
        participant Err as GlobalExceptionHandler
    end
    box rgba(219,234,254,0.6) Application
        participant Svc as RequestService
        participant Pub as IRabbitMQSender<br/>(puerto)
    end
    box rgba(255,243,196,0.8) Domain
        participant Conv as Convocation
        participant St as OpenState
        participant Req as Request
        participant CReq as ConvocationRequirement
        participant Port as IStudentRepository<br/>IConvocationRepository<br/>IRequestRepository...<br/>(puertos)
    end
    box rgba(229,231,235,0.6) Infrastructure
        participant Repo as Repositorios JPA<br/>(adaptadores)
        participant Snd as RabbitMQSender<br/>(adaptador)
    end
    participant DB as SQL Server
    participant MQ as RabbitMQ

    Est->>Ctrl: POST /request/createRequest
    Note over Ctrl: el studentId sale del token<br/>(@AuthenticationPrincipal), no del JSON
    Ctrl->>Svc: createRequest(RequestDto, studentId)
    Svc->>Port: studentRepository.findById(studentId)
    Port->>Repo: implementado por
    Repo->>DB: SELECT Student
    Repo-->>Svc: Student (o NotFoundException → 404)
    Svc->>Port: convocationRepository.findById(convocationId)
    Port->>Repo: implementado por
    Repo-->>Svc: Convocation (o NotFoundException → 404)
    Svc->>Conv: applyDateRules(hoy)
    Svc->>Conv: ensureCanReceiveRequests(hoy)
    Conv->>St: ensureCanReceiveRequests(this, hoy)
    alt fuera de las fechas o en otro estado
        St-->>Svc: InvalidConvocationStateException
        Svc-->>Err: la excepción sube
        Err-->>Est: 400 { state: "La convocatoria aún no recibe solicitudes: abre el 15/11/2026" }
    else recibe solicitudes
        Svc->>Port: requestRepository.existsByStudentIdAndConvocationId(...)
        Port->>Repo: implementado por
        Repo-->>Svc: si ya existe → ConflictException → 409
        Svc->>Port: convocationRequirementRepository.findConvocationRequirements(id)
        Port->>Repo: implementado por
        Repo-->>Svc: requisitos de la convocatoria
        Svc->>Req: validateAgainst(requisitos)
        Note over Req: falta un requisito, está repetido<br/>o no es de esta convocatoria<br/>→ DomainValidationException → 400
        Svc->>Req: verifyRequirements(requisitos)
        loop cada requisito
            Req->>CReq: isMetBy(valor del estudiante)
        end
        Note over Req: cumple todos → ACTIVE<br/>si no → PENDING<br/>nunca se rechaza sola
        Req-->>Svc: requisitos no cumplidos
        Svc->>Port: requestRepository.save(request)
        Port->>Repo: implementado por
        Repo->>DB: INSERT Request
        Svc->>Port: requestRequirementRepository.saveAll(respuestas)
        Port->>Repo: implementado por
        Repo->>DB: INSERT RequestRequirement
        Svc->>Pub: publishRequestStatusChanged(evento)
        Pub->>Snd: implementado por
        Note over Snd: se registra para después del commit
        Svc-->>Ctrl: RequestDto con state y unmetRequirements
        Note over Svc,Repo: commit de la transacción (@Transactional)
        Snd->>MQ: REQUEST_STATUS_CHANGED (nuevo estado ACTIVE o PENDING)
        alt cumple todos
            Ctrl-->>Est: 200 "Su solicitud ha sido creada exitosamente y cumple con todos los requisitos"
        else no cumple alguno
            Ctrl-->>Est: 200 "...está pendiente de aprobación aunque no cumples con todos los requisitos"
        end
    end
```

Las flechas que terminan en una excepción cortan el flujo: no se ejecuta nada de lo que sigue. Como todas las comprobaciones van antes del primer `INSERT`, una solicitud inválida nunca llega a la base. Y como el evento sale **después del commit**, el estudiante nunca recibe un correo de una solicitud que no quedó guardada.

## 4. Aprobar o rechazar una solicitud

Cuando la convocatoria está cerrada, el administrador decide las solicitudes. La regla tiene dos partes y cada una vive en un objeto distinto del dominio: **la solicitud** debe estar en revisión (`ReviewRequestState`, que agrupa `PENDING` y `ACTIVE`) y **la convocatoria** debe estar cerrada (`ClosedState.ensureCanDecideRequests`). La solicitud le pregunta a la convocatoria; el servicio no revisa ninguna de las dos cosas.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    box rgba(229,231,235,0.6) Api
        participant Ctrl as RequestController
        participant Err as GlobalExceptionHandler
    end
    box rgba(219,234,254,0.6) Application
        participant Svc as RequestService
        participant Pub as IRabbitMQSender<br/>(puerto)
    end
    box rgba(255,243,196,0.8) Domain
        participant Req as Request
        participant RSt as Estado de la solicitud<br/>RequestStates.of(...)
        participant Conv as Convocation
        participant CSt as Estado de la convocatoria<br/>ConvocationStates.of(...)
        participant Port as IRequestRepository<br/>IConvocationRepository...<br/>(puertos)
    end
    box rgba(229,231,235,0.6) Infrastructure
        participant Repo as Repositorios JPA<br/>(adaptadores)
        participant Snd as RabbitMQSender<br/>(adaptador)
    end
    participant MQ as RabbitMQ
    participant Cons as Consumidor de email<br/>(clean-architecture-notification)
    actor Est as Estudiante

    Admin->>Ctrl: PUT /request/approveRequest/7
    Ctrl->>Svc: approveRequest(7)
    Svc->>Port: requestRepository.findById(7) y la convocatoria
    Port->>Repo: implementado por
    Repo-->>Svc: Request y Convocation (con applyDateRules)
    Svc->>Req: approve(convocation)
    Req->>RSt: approve(this, convocation)
    alt la solicitud ya está decidida o retirada
        RSt-->>Svc: InvalidRequestStateException
        Svc-->>Err: la excepción sube
        Err-->>Admin: 400 { state: "No se puede aprobar una solicitud aprobada" }
    else está en revisión (ReviewRequestState)
        RSt->>Conv: ensureCanDecideRequests()
        Conv->>CSt: ensureCanDecideRequests(this)
        alt la convocatoria sigue abierta
            CSt-->>Svc: InvalidConvocationStateException
            Svc-->>Err: la excepción sube
            Err-->>Admin: 400 { state: "La convocatoria sigue abierta: ciérrela para poder aprobar o rechazar sus solicitudes" }
        else está cerrada (ClosedState)
            RSt->>Req: setState(ACCEPT)
            Svc->>Port: requestRepository.save(request)
            Port->>Repo: implementado por
            Svc->>Pub: publishRequestStatusChanged(evento)
            Pub->>Snd: implementado por
            Note over Snd: se registra para después del commit
            Svc-->>Ctrl: RequestDetailDto
            Note over Svc,Repo: commit de la transacción
            Snd->>MQ: exchange event.drivent.exchange<br/>routing key "email"
            Ctrl-->>Admin: 200 "Solicitud aprobada"
            MQ-)Cons: mensaje en la cola event.drivent.email
            Cons-)Est: correo con la decisión (SMTP)
        end
    end
```

**Rechazar** (`PUT /request/rejectRequest/{id}` con `{ "comment": "..." }`) sigue el mismo camino con `reject(convocation, comment)`. Además, la justificación es obligatoria dos veces: `@NotBlank` en `RejectRequestDto` (capa Api) y `ReviewRequestState.reject` en el dominio, que la guarda en `Request.comment` y pasa la solicitud a `REJECT`.

El administrador recibe la respuesta sin esperar a que salga el correo. Si el servicio de notificaciones está caído, el mensaje espera en la cola. Si el caído es RabbitMQ, la decisión se guarda igual y el backend registra en el log el evento que no pudo publicar. El detalle del mensaje está en el [README](../README.md#eventos-de-notificación).

## 5. Retirar una solicitud

El estudiante retira una solicitud mientras está en revisión. Hay dos reglas: que sea **su** solicitud (`Request.belongsTo`) y que el estado lo permita (`ReviewRequestState.cancel`). La solicitud no se borra: queda `CANCELLED` para el historial.

```mermaid
sequenceDiagram
    autonumber
    actor Est as Estudiante
    box rgba(229,231,235,0.6) Api
        participant Ctrl as RequestController
        participant Err as GlobalExceptionHandler
    end
    box rgba(219,234,254,0.6) Application
        participant Svc as RequestService
        participant Pub as IRabbitMQSender<br/>(puerto)
    end
    box rgba(255,243,196,0.8) Domain
        participant Req as Request
        participant States as RequestStates<br/>(fábrica)
        participant RSt as Estado de la solicitud<br/>ReviewRequestState, AcceptedRequestState...
        participant Port as IRequestRepository<br/>(puerto)
    end
    box rgba(229,231,235,0.6) Infrastructure
        participant Repo as RequestRepository<br/>(adaptador JPA)
        participant Snd as RabbitMQSender<br/>(adaptador)
    end
    participant DB as SQL Server

    Est->>Ctrl: PUT /request/cancelRequest/7
    Note over Ctrl: el studentId sale del token
    Ctrl->>Svc: cancelRequest(7, studentId)
    Svc->>Port: findById(7)
    Port->>Repo: implementado por
    Repo->>DB: SELECT Request
    Repo-->>Svc: Request (o NotFoundException → 404)
    Svc->>Req: belongsTo(studentId)
    alt es de otro estudiante
        Req-->>Svc: false
        Svc-->>Err: ForbiddenException (de Application)
        Err-->>Est: 403 "Solo el estudiante que hizo la solicitud puede retirarla"
    else es suya
        Req-->>Svc: true
        Svc->>Req: cancel()
        Req->>States: of(state)
        States-->>Req: el objeto del estado actual
        Req->>RSt: cancel(this)
        alt ya fue decidida o retirada
            RSt-->>Svc: InvalidRequestStateException
            Svc-->>Err: la excepción sube
            Err-->>Est: 400 { state: "No se puede retirar una solicitud aprobada" }
        else está en revisión (ReviewRequestState)
            RSt->>Req: setState(CANCELLED)
            Svc->>Port: save(request)
            Port->>Repo: implementado por
            Repo->>DB: UPDATE Request
            Svc->>Pub: publishRequestStatusChanged(evento, estado anterior)
            Pub->>Snd: implementado por (sale después del commit)
            Svc-->>Ctrl: StudentRequestDto
            Ctrl-->>Est: 200 "Solicitud retirada"
        end
    end
```

**Qué mostrar de aquí:** `belongsTo` es una pregunta al dominio ("¿esta solicitud es de este estudiante?"), pero la respuesta "no tienes permiso" es de Application (`ForbiddenException`), porque es una regla de acceso y no del negocio. Lo que sí es del negocio, que una solicitud aprobada ya no se puede retirar, lo decide el estado.

## 6. Iniciar sesión y usar el token

El login usa tres puertos: `IUserRepository` (del dominio, porque `User` es una entidad) e `IPasswordHasher` e `ITokenProvider` (de Application, porque son detalles técnicos que el caso de uso necesita). BCrypt y JWT quedan en Infrastructure, así que se podrían cambiar sin tocar `AuthService`.

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant Front as Front React<br/>AuthProvider
    box rgba(229,231,235,0.6) Api
        participant Filter as JwtAuthenticationFilter<br/>SecurityConfig
        participant Ctrl as AuthController<br/>RequestController
        participant Err as GlobalExceptionHandler<br/>SecurityErrorWriter
    end
    box rgba(219,234,254,0.6) Application
        participant Svc as AuthService
        participant Hash as IPasswordHasher<br/>(puerto)
        participant Tok as ITokenProvider<br/>(puerto)
    end
    box rgba(255,243,196,0.8) Domain
        participant UPort as IUserRepository<br/>(puerto)
        participant User as User
    end
    box rgba(229,231,235,0.6) Infrastructure
        participant URepo as UserRepository<br/>(adaptador JPA)
        participant BCrypt as BCryptPasswordHasher
        participant Jwt as JwtTokenProvider
    end
    participant DB as SQL Server

    rect rgba(240,240,240,0.4)
    Note over U,DB: Iniciar sesión
    U->>Front: correo y contraseña
    Front->>Ctrl: POST /auth/login (ruta pública)
    Ctrl->>Svc: login(LoginDto)
    Svc->>UPort: findByEmail(correo)
    UPort->>URepo: implementado por
    URepo->>DB: SELECT Users
    URepo-->>Svc: Optional de User
    Svc->>Hash: matches(contraseña, hash)
    Hash->>BCrypt: implementado por
    Note over Svc: si el correo no existe compara contra<br/>un hash falso, para tardar lo mismo
    alt correo desconocido o contraseña incorrecta
        Svc-->>Err: UnauthorizedException
        Err-->>Front: 401 "Correo o contraseña incorrectos"
    else credenciales válidas
        Svc->>User: getRole(), getStudentId()
        Svc->>Tok: issue(usuario autenticado)
        Tok->>Jwt: implementado por (HS256, vence en 8 horas)
        Svc-->>Ctrl: SessionDto con token, rol y nombre
        Ctrl-->>Front: 200 "Sesión iniciada"
        Front-->>U: guarda la sesión y abre el menú de su rol
    end
    end

    rect rgba(240,240,240,0.4)
    Note over U,DB: Cada petición siguiente
    Front->>Filter: GET /request/getMyRequests<br/>Authorization: Bearer token
    Filter->>Tok: read(token)
    Tok->>Jwt: implementado por
    alt token vencido, alterado o firmado con otra llave
        Jwt-->>Filter: Optional vacío
        Filter-->>Err: la ruta no es pública y no hay usuario
        Err-->>Front: 401 "La sesión no es válida o venció: inicie sesión de nuevo"
        Front-->>U: cierra la sesión y vuelve al login
    else token válido pero el rol no puede usar la ruta
        Jwt-->>Filter: usuario con ROLE_ADMIN
        Err-->>Front: 403 "No tiene permiso para usar este recurso"
    else token válido y rol correcto
        Jwt-->>Filter: usuario con ROLE_STUDENT
        Filter->>Ctrl: getMyRequests(usuario)
        Ctrl-->>Front: 200 con sus solicitudes
    end
    end
```

En el login el dominio aporta la entidad `User` y su puerto, pero no lanza ninguna excepción: la regla *"correo o contraseña incorrectos"* es del caso de uso (`UnauthorizedException`, de Application). El mismo mensaje sirve para un correo que no existe y para una contraseña equivocada, así no se puede averiguar quién tiene cuenta.
