# Diagramas de secuencia

Un diagrama de secuencia se lee de arriba hacia abajo, como una conversación: cada flecha es una llamada de un objeto a otro y el número indica el orden. Aquí se usan para seguir una petición desde que el usuario hace clic hasta que el dato queda en SQL Server.

Los participantes están agrupados en cajas de color, una por capa de Clean Architecture:

| Caja | Qué contiene |
|---|---|
| Api (gris) | Controladores, el filtro JWT y `GlobalExceptionHandler`. Reciben HTTP y responden HTTP. |
| Application (azul) | Servicios y mappers, que coordinan el caso de uso. También los puertos técnicos: `IRabbitMQSender`, `IPasswordHasher` e `ITokenProvider`. |
| Domain (amarillo) | Entidades, objetos de estado, puertos de los repositorios y excepciones del negocio. Aquí se decide si algo está permitido. |
| Infrastructure (gris) | Los adaptadores: repositorios JPA, el publicador de RabbitMQ, BCrypt y JWT. |

SQL Server, RabbitMQ y el front quedan por fuera porque son otros procesos.

**Qué observar en todos.** La caja Domain nunca llama a algo que esté fuera de ella. Cuando el servicio necesita la base de datos le habla al puerto (por ejemplo `IConvocationRepository`), que pertenece al dominio, y es Spring quien conecta ese puerto con su implementación en Infrastructure. Por eso verás la flecha *implementado por* entre el puerto y el repositorio JPA. Lo mismo pasa con los errores: el dominio lanza una excepción sin saber nada de HTTP y es `GlobalExceptionHandler`, en la capa Api, quien la convierte en un 400.

Los diagramas son seis:

1. [Crear una convocatoria](#1-crear-una-convocatoria-cu-001)
2. [Cambiar el estado de una convocatoria](#2-cambiar-el-estado-de-una-convocatoria-patrón-state)
3. [Crear una solicitud](#3-crear-una-solicitud-cu-002)
4. [Aprobar o rechazar una solicitud](#4-aprobar-o-rechazar-una-solicitud)
5. [Retirar una solicitud](#5-retirar-una-solicitud)
6. [Iniciar sesión y usar el token](#6-iniciar-sesión-y-usar-el-token)

Del 1 al 5 se supone que la petición ya pasó la seguridad. El [diagrama 6](#6-iniciar-sesión-y-usar-el-token) muestra cómo se revisa el token.

## 1. Crear una convocatoria (CU-001)

El administrador envía el formulario. Hay dos validaciones distintas y conviene no confundirlas: `@Valid` revisa el formato de los campos en la capa Api (que el título no esté vacío, por ejemplo), y la entidad `Convocation` revisa las reglas del negocio (que las fechas tengan sentido y que los requisitos existan).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    participant Front as Front React
    box rgba(128,128,128,0.14) Api
        participant Ctrl as ConvocationController
        participant Err as GlobalExceptionHandler
    end
    box rgba(59,130,246,0.16) Application
        participant Svc as ConvocationService
        participant Map as ConvocationMapper
    end
    box rgba(234,179,8,0.20) Domain
        participant Conv as Convocation
        participant CReq as ConvocationRequirement
        participant Port as IRequirementRepository<br/>IConvocationRepository<br/>IConvocationRequirementRepository<br/>(puertos)
    end
    box rgba(128,128,128,0.14) Infrastructure
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

Fíjate en que el controlador no valida nada por su cuenta y que el servicio tampoco sabe validar fechas: se lo pide a la entidad. Como `validate` se ejecuta antes del primer `INSERT`, una convocatoria inválida nunca llega a la base. Y si fallara el segundo `INSERT`, `@Transactional` deshace también el primero.

## 2. Cambiar el estado de una convocatoria (patrón State)

El ejemplo es cerrar, pero reabrir, finalizar y cancelar recorren el mismo camino porque todas pasan por el método `changeState` del servicio: busca la convocatoria, deja que su estado aplique la acción y guarda.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    box rgba(128,128,128,0.14) Api
        participant Ctrl as ConvocationController
        participant Err as GlobalExceptionHandler
    end
    box rgba(59,130,246,0.16) Application
        participant Svc as ConvocationService
    end
    box rgba(234,179,8,0.20) Domain
        participant Conv as Convocation
        participant States as ConvocationStates<br/>(fábrica)
        participant St as Estado actual<br/>OpenState, ClosedState...
        participant Port as IConvocationRepository<br/>(puerto)
    end
    box rgba(128,128,128,0.14) Infrastructure
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

Lo que cambia entre las cuatro acciones es solo lo que revisa el estado:

| Acción | Endpoint | Qué revisa el dominio | Error si no se cumple |
|---|---|---|---|
| Reabrir | `PUT /convocation/reopenConvocation/{id}` | Solo una convocatoria cerrada se reabre, y con una fecha de fin posterior a hoy. | *"La nueva fecha de fin debe ser posterior a la fecha actual"* |
| Finalizar | `PUT /convocation/finishConvocation/{id}` | Tiene que estar cerrada y sin solicitudes por decidir. El servicio consulta eso antes y le pasa el resultado al dominio. | *"No se puede finalizar: hay solicitudes sin aprobar ni rechazar"* |
| Cancelar | `PUT /convocation/cancelConvocation/{id}` | Abierta o cerrada, siempre con justificación. | *"La justificación de la cancelación es obligatoria"* |

Busca en `ConvocationService` un `if` que pregunte por el estado: no hay ninguno. Quien decide es el objeto que entrega `ConvocationStates.of(...)`. Si mañana apareciera un estado nuevo, se crearía su clase y el servicio quedaría igual.

## 3. Crear una solicitud (CU-002)

El estudiante se postula dando un valor para cada requisito. Las comprobaciones van de la más barata a la más costosa: primero que existan el estudiante y la convocatoria, luego que la convocatoria esté recibiendo solicitudes, después que no haya una solicitud repetida y por último que las respuestas estén completas. El id del estudiante sale del token y no del JSON, así nadie puede postularse a nombre de otro.

```mermaid
sequenceDiagram
    autonumber
    actor Est as Estudiante
    box rgba(128,128,128,0.14) Api
        participant Ctrl as RequestController
        participant Err as GlobalExceptionHandler
    end
    box rgba(59,130,246,0.16) Application
        participant Svc as RequestService
        participant Pub as IRabbitMQSender<br/>(puerto)
    end
    box rgba(234,179,8,0.20) Domain
        participant Conv as Convocation
        participant St as OpenState
        participant Req as Request
        participant CReq as ConvocationRequirement
        participant Port as IStudentRepository<br/>IConvocationRepository<br/>IRequestRepository...<br/>(puertos)
    end
    box rgba(128,128,128,0.14) Infrastructure
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

Cada flecha que termina en una excepción corta el flujo y lo que sigue ya no se ejecuta. El evento para el correo se registra antes de responder, pero sale después del commit, de modo que el estudiante nunca recibe un correo de una solicitud que no se guardó.

## 4. Aprobar o rechazar una solicitud

Para decidir una solicitud se deben cumplir dos condiciones y cada una la revisa un objeto distinto. La solicitud tiene que estar en revisión, y eso lo sabe su estado (`ReviewRequestState`, compartido por `PENDING` y `ACTIVE`). La convocatoria tiene que estar cerrada, y eso lo sabe el estado de la convocatoria. La solicitud le pregunta a la convocatoria y el servicio no revisa ninguna de las dos.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    box rgba(128,128,128,0.14) Api
        participant Ctrl as RequestController
        participant Err as GlobalExceptionHandler
    end
    box rgba(59,130,246,0.16) Application
        participant Svc as RequestService
        participant Pub as IRabbitMQSender<br/>(puerto)
    end
    box rgba(234,179,8,0.20) Domain
        participant Req as Request
        participant RSt as Estado de la solicitud<br/>RequestStates.of(...)
        participant Conv as Convocation
        participant CSt as Estado de la convocatoria<br/>ConvocationStates.of(...)
        participant Port as IRequestRepository<br/>IConvocationRepository...<br/>(puertos)
    end
    box rgba(128,128,128,0.14) Infrastructure
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

Rechazar funciona igual, con `reject(convocation, comment)`. La justificación se exige dos veces: `@NotBlank` en `RejectRequestDto` y otra vez en `ReviewRequestState.reject`, que la guarda en `Request.comment`.

El administrador recibe su respuesta sin esperar el correo. Si el servicio de notificaciones está apagado, el mensaje espera en la cola; si el apagado es RabbitMQ, la decisión se guarda de todas formas y el backend deja el evento perdido en el log. El formato del mensaje está en el [README](../README.md#eventos-de-notificación).

## 5. Retirar una solicitud

El estudiante puede retirar una solicitud mientras sigue en revisión. Aquí hay dos reglas de naturaleza distinta: que la solicitud sea suya y que su estado lo permita.

```mermaid
sequenceDiagram
    autonumber
    actor Est as Estudiante
    box rgba(128,128,128,0.14) Api
        participant Ctrl as RequestController
        participant Err as GlobalExceptionHandler
    end
    box rgba(59,130,246,0.16) Application
        participant Svc as RequestService
        participant Pub as IRabbitMQSender<br/>(puerto)
    end
    box rgba(234,179,8,0.20) Domain
        participant Req as Request
        participant States as RequestStates<br/>(fábrica)
        participant RSt as Estado de la solicitud<br/>ReviewRequestState, AcceptedRequestState...
        participant Port as IRequestRepository<br/>(puerto)
    end
    box rgba(128,128,128,0.14) Infrastructure
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

Vale la pena comparar los dos errores. `belongsTo` es una pregunta al dominio, pero el "no tienes permiso" lo lanza Application (`ForbiddenException`, 403) porque es una regla de acceso. En cambio, que una solicitud aprobada ya no se pueda retirar es una regla del negocio, y por eso la decide el estado. La solicitud retirada no se borra: queda `CANCELLED` para el historial.

## 6. Iniciar sesión y usar el token

El login usa tres puertos. `IUserRepository` está en Domain porque `User` es una entidad. `IPasswordHasher` e `ITokenProvider` están en Application porque son herramientas técnicas que el caso de uso necesita, no reglas del negocio. BCrypt y JWT viven en Infrastructure y se podrían cambiar sin tocar `AuthService`.

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant Front as Front React<br/>AuthProvider
    box rgba(128,128,128,0.14) Api
        participant Filter as JwtAuthenticationFilter<br/>SecurityConfig
        participant Ctrl as AuthController<br/>RequestController
        participant Err as GlobalExceptionHandler<br/>SecurityErrorWriter
    end
    box rgba(59,130,246,0.16) Application
        participant Svc as AuthService
        participant Hash as IPasswordHasher<br/>(puerto)
        participant Tok as ITokenProvider<br/>(puerto)
    end
    box rgba(234,179,8,0.20) Domain
        participant UPort as IUserRepository<br/>(puerto)
        participant User as User
    end
    box rgba(128,128,128,0.14) Infrastructure
        participant URepo as UserRepository<br/>(adaptador JPA)
        participant BCrypt as BCryptPasswordHasher
        participant Jwt as JwtTokenProvider
    end
    participant DB as SQL Server

    rect rgba(128,128,128,0.08)
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

    rect rgba(128,128,128,0.08)
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

En este caso el dominio no lanza ninguna excepción. Comprobar las credenciales es trabajo del caso de uso, y el error es `UnauthorizedException`, de Application. El mensaje es el mismo para un correo que no existe y para una contraseña equivocada, así nadie puede averiguar quién tiene cuenta probando correos.
