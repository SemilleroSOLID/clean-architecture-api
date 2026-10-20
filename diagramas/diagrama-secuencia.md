# Diagramas de secuencia

Estos diagramas muestran qué pasa, en orden, desde que el usuario hace clic en el front hasta que el dato queda guardado en SQL Server. Sirven para ver la arquitectura funcionando: cada petición entra por la capa Api, la capa Application coordina, el dominio decide si la operación es válida y la infraestructura guarda.

Los participantes tienen el nombre de la clase real y la capa a la que pertenece, para poder abrir el código mientras se lee el diagrama.

1. [Crear una convocatoria](#1-crear-una-convocatoria-cu-001) (CU-001)
2. [Cambiar el estado de una convocatoria](#2-cambiar-el-estado-de-una-convocatoria-patrón-state) (patrón State)
3. [Crear una solicitud](#3-crear-una-solicitud-cu-002) (CU-002)
4. [Aprobar una solicitud y notificar al estudiante](#4-aprobar-una-solicitud-y-notificar-al-estudiante) (eventos)

## 1. Crear una convocatoria (CU-001)

El administrador llena el formulario y lo envía. Hay dos niveles de validación: el formato de los campos lo revisa Bean Validation antes de entrar al servicio, y las reglas de negocio (fechas, requisitos) las revisa la entidad `Convocation`.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    participant Front as Front React<br/>convocation.service.js
    participant Ctrl as ConvocationController<br/>(Api)
    participant Svc as ConvocationService<br/>(Application)
    participant Map as ConvocationMapper<br/>(Application)
    participant Conv as Convocation<br/>(Domain)
    participant Repo as Repositorios<br/>(Infrastructure)
    participant DB as SQL Server

    Admin->>Front: Llena el formulario y confirma
    Front->>Ctrl: POST /convocation/createConvocation (JSON)
    Note over Ctrl: @Valid revisa campos obligatorios y formatos
    Ctrl->>Svc: createConvocation(ConvocationDto)
    Svc->>Map: toDomain(dto)
    Map-->>Svc: Convocation
    Svc->>Repo: requirementRepository.findAll()
    Repo->>DB: SELECT Requirement
    DB-->>Repo: filas
    Repo-->>Svc: lista de Requirement
    Svc->>Conv: validate(hoy, catálogo)
    alt Alguna regla no se cumple
        Conv-->>Svc: DomainValidationException (campo → mensaje)
        Svc-->>Ctrl: la excepción sube sin capturarse
        Note over Ctrl: GlobalExceptionHandler la convierte en 400
        Ctrl-->>Front: 400 { data: { startDate: "..." } }
        Front-->>Admin: Muestra el error debajo de cada campo
    else Todo es válido
        Svc->>Conv: setState(OPEN)
        Svc->>Repo: convocationRepository.save(convocation)
        Note over Repo: ConvocationPersistenceMapper<br/>la convierte en ConvocationEntity
        Repo->>DB: INSERT Convocation
        DB-->>Repo: id generado
        Repo-->>Svc: Convocation con id
        Svc->>Repo: convocationRequirementRepository.saveAll(requisitos)
        Repo->>DB: INSERT ConvocationRequirement
        Svc->>Map: toDto(convocation)
        Svc-->>Ctrl: ConvocationDto
        Ctrl-->>Front: 200 CustomResponse { data, message }
        Front-->>Admin: "Convocatoria creada" y vuelve a la lista
    end
```

**Qué mostrar de aquí:** el controlador no tiene ninguna regla, solo recibe y responde. El servicio no sabe validar fechas, se lo pide a la entidad. Y el dominio no sabe que existe HTTP: lanza una excepción con un mapa de errores y es la capa Api la que decide que eso es un 400.

## 2. Cambiar el estado de una convocatoria (patrón State)

Ejemplo con *cerrar*. Reabrir, finalizar y cancelar siguen el mismo camino, porque todas pasan por el método privado `changeState` del servicio.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    participant Ctrl as ConvocationController<br/>(Api)
    participant Svc as ConvocationService<br/>(Application)
    participant Repo as ConvocationRepository<br/>(Infrastructure)
    participant Conv as Convocation<br/>(Domain)
    participant States as ConvocationStates<br/>(Domain)
    participant State as OpenState / ClosedState ...<br/>(Domain)

    Admin->>Ctrl: PUT /convocation/closeConvocation/{id}
    Ctrl->>Svc: closeConvocation(id)
    Svc->>Repo: findById(id)
    alt No existe
        Repo-->>Svc: Optional vacío
        Svc-->>Ctrl: NotFoundException → 404
    else Existe
        Repo-->>Svc: Convocation
        Svc->>Conv: applyDateRules(hoy)
        Conv->>States: of(state)
        States-->>Conv: estado actual
        Conv->>State: applyDateRules(this, hoy)
        Note over State: Si está abierta y ya pasó<br/>la fecha de fin, pasa a CLOSED
        Svc->>Conv: close()
        Conv->>States: of(state)
        States-->>Conv: estado actual
        Conv->>State: close(this)
        alt El estado permite cerrar (OpenState)
            State->>Conv: setState(CLOSED)
            Svc->>Repo: save(convocation)
            Svc-->>Ctrl: ConvocationDto con estado CLOSED
            Ctrl-->>Admin: 200 "Convocatoria cerrada"
        else El estado no lo permite (Closed, Finished, Cancelled)
            State-->>Svc: InvalidConvocationStateException<br/>"No se puede cerrar una convocatoria cerrada"
            Svc-->>Ctrl: la excepción sube
            Ctrl-->>Admin: 400 { state: "No se puede cerrar..." }
        end
    end
```

**Qué mostrar de aquí:** en `ConvocationService` no hay ningún `if (estado == ...)`. El servicio carga, delega y guarda. Quien decide si la acción es válida es el objeto del estado actual, y si mañana se agrega un estado nuevo, se crea su clase sin tocar el servicio.

## 3. Crear una solicitud (CU-002)

El estudiante se postula a una convocatoria con un valor para cada requisito. Las comprobaciones van de la más barata a la más costosa: primero que existan el estudiante y la convocatoria, después que la convocatoria esté recibiendo solicitudes, después que no haya una solicitud repetida y al final que las respuestas estén completas.

```mermaid
sequenceDiagram
    autonumber
    actor Est as Estudiante
    participant Ctrl as RequestController<br/>(Api)
    participant Svc as RequestService<br/>(Application)
    participant Repos as Repositorios<br/>(Infrastructure)
    participant Conv as Convocation<br/>(Domain)
    participant Req as Request<br/>(Domain)
    participant DB as SQL Server

    Est->>Ctrl: POST /request/createRequest
    Ctrl->>Svc: createRequest(RequestDto)
    Svc->>Repos: studentRepository.existsById(studentId)
    Repos-->>Svc: false → NotFoundException (404)
    Svc->>Repos: convocationRepository.findById(convocationId)
    Repos-->>Svc: Convocation
    Svc->>Conv: applyDateRules(hoy)
    Svc->>Conv: ensureCanReceiveRequests(hoy)
    Note over Conv: El estado decide: solo OpenState<br/>y dentro de las fechas
    Conv-->>Svc: o InvalidConvocationStateException (400)<br/>"aún no recibe solicitudes: abre el dd/MM/yyyy"
    Svc->>Repos: existsByStudentIdAndConvocationId(...)
    Repos-->>Svc: true → ConflictException (409)
    Svc->>Repos: findConvocationRequirements(convocationId)
    Repos-->>Svc: lista de ConvocationRequirement
    Svc->>Req: validateAgainst(requisitos)
    Note over Req: Falta un requisito, está repetido<br/>o no es de esta convocatoria → 400
    Svc->>Req: setCreationDate(hoy)
    Svc->>Req: verifyRequirements(requisitos)
    Note over Req: Compara cada valor con su condición.<br/>Cumple todos → ACTIVE · si no → PENDING<br/>Nunca REJECT
    Req-->>Svc: requisitos no cumplidos
    Svc->>Repos: requestRepository.save(request)
    Repos->>DB: INSERT Request
    Svc->>Repos: requestRequirementRepository.saveAll(respuestas)
    Repos->>DB: INSERT RequestRequirement
    Svc-->>Ctrl: RequestDto con state y unmetRequirements
    alt Cumple todos
        Ctrl-->>Est: 200 "Su solicitud ha sido creada exitosamente..."
    else No cumple alguno
        Ctrl-->>Est: 200 "...está pendiente de aprobación aunque no cumples con todos los requisitos"
    end
```

Las flechas que terminan en una excepción cortan el flujo: no se ejecuta nada de lo que sigue. Como todas las comprobaciones van antes del primer `INSERT`, una solicitud inválida nunca llega a la base de datos, y si algo falla al guardar los requisitos, `@Transactional` deshace también la solicitud.

## 4. Aprobar una solicitud y notificar al estudiante

Aprobar y rechazar ya está implementado ([#17](https://github.com/SemilleroSOLID/clean-architecture-api/issues/17)): `PUT /request/approveRequest/{id}` y `PUT /request/rejectRequest/{id}`, con la regla de la convocatoria cerrada resuelta por su patrón State (`ensureCanDecideRequests`). Desde [#20](https://github.com/SemilleroSOLID/clean-architecture-api/issues/20) cada cambio publica el evento `REQUEST_STATUS_CHANGED` (también al crear y al retirar); falta enviar el correo real ([#21](https://github.com/SemilleroSOLID/clean-architecture-api/issues/21)). Lo nuevo es la comunicación por eventos: el backend no envía el correo, publica un mensaje en RabbitMQ y el microservicio de notificaciones lo procesa por su cuenta.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    participant Ctrl as RequestController<br/>(Api)
    participant Svc as RequestService<br/>(Application)
    participant Req as Request<br/>(Domain)
    participant Repo as RequestRepository<br/>(Infrastructure)
    participant Pub as RabbitMQSender<br/>(Infrastructure)
    participant MQ as RabbitMQ
    participant Cons as Consumidor de email<br/>(clean-architecture-notification)
    actor Est as Estudiante

    Admin->>Ctrl: PUT aprobar o rechazar con justificación
    Ctrl->>Svc: approveRequest(id) / rejectRequest(id, comment)
    Svc->>Req: approve(convocation) / reject(convocation, comment)
    Note over Req: Solo si la solicitud no está decidida<br/>y la convocatoria está cerrada (ensureCanDecideRequests)
    Svc->>Repo: save(request)
    Svc->>Pub: publishRequestStatusChanged(evento JSON)
    Note over Svc,Pub: Svc solo conoce la interfaz de Application,<br/>RabbitMQ es un detalle de Infrastructure
    Note over Pub: Se registra para después del commit
    Svc-->>Ctrl: RequestDetailDto
    Note over Svc,Repo: commit de la transacción
    Pub->>MQ: exchange event.drivent.exchange<br/>routing key "email"
    Ctrl-->>Admin: 200
    MQ-)Cons: mensaje en la cola event.drivent.email
    Cons-)Est: correo con la decisión (#21)
```

El administrador recibe la respuesta sin esperar a que salga el correo. Si el servicio de notificaciones está caído, el mensaje queda en la cola y se envía cuando vuelva. Si el caído es RabbitMQ, la decisión se guarda igual y el backend registra en el log el evento que no pudo publicar. El mensaje completo está en el [README](../README.md#eventos-de-notificación).
