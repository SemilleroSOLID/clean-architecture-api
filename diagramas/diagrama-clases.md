# Diagrama de clases

El backend tiene más de 130 clases y en un solo diagrama no se leerían. Por eso esta página tiene varios, cada uno sobre un tema:

1. [Clases por capas](#1-clases-por-capas-convocatoria): la convocatoria pasando por las cuatro capas. Sirve para ver hacia dónde apuntan las dependencias.
2. [Modelo de dominio](#2-modelo-de-dominio): las entidades del negocio y cómo se relacionan.
3. [Solicitudes: estados y eventos](#3-solicitudes-estados-y-eventos): el patrón State de la solicitud y el evento que va a RabbitMQ.
4. [Seguridad](#4-seguridad-login-y-jwt): el login y el token JWT.
5. [Patrón State de la convocatoria](#5-patrón-state-de-la-convocatoria): un resumen, el detalle está en [estados-convocatoria.md](estados-convocatoria.md#implementación-patrón-state).

Las clases de solicitudes y estudiantes siguen el mismo molde que las de convocatoria: controlador, interfaz del servicio, servicio, mapper de DTO, interfaz del repositorio, repositorio, mapper de persistencia, CRUD de Spring Data y entidad JPA. Si entiendes el primer diagrama, entiendes cómo está hecho el resto.

## 1. Clases por capas (convocatoria)

```mermaid
classDiagram
    direction TB

    namespace Api {
        class ConvocationController {
            -IConvocationService convocationService
            +getAllConvocations() CustomResponse
            +getConvocationById(id) CustomResponse
            +createConvocation(ConvocationDto) CustomResponse
            +updateConvocation(id, ConvocationDto) CustomResponse
            +deleteConvocation(id) CustomResponse
            +closeConvocation(id) CustomResponse
            +reopenConvocation(id, ReopenConvocationDto) CustomResponse
            +finishConvocation(id) CustomResponse
            +cancelConvocation(id, CancelConvocationDto) CustomResponse
        }
        class GlobalExceptionHandler {
            +handleNotFound() 404
            +handleUnauthorized() 401
            +handleForbidden() 403
            +handleConflict() 409
            +handleDomainValidation() 400
            +handleInvalidArguments() 400
        }
        class CustomResponse~T~ {
            +T data
            +int codeState
            +String message
        }
    }

    namespace Application {
        class IConvocationService {
            <<interface>>
            +getAllConvocation() List~ConvocationDto~
            +getConvocationById(id) ConvocationDto
            +createConvocation(ConvocationDto) ConvocationDto
            +updateConvocation(id, ConvocationDto) ConvocationDto
            +deleteConvocation(id)
            +closeConvocation(id) ConvocationDto
            +reopenConvocation(id, newEndDate) ConvocationDto
            +finishConvocation(id) ConvocationDto
            +cancelConvocation(id, reason) ConvocationDto
        }
        class ConvocationService {
            -IConvocationRepository convocationRepository
            -IRequestRepository requestRepository
            -IConvocationMapper convocationMapper
            -Clock clock
            -findConvocation(id) Convocation
            -changeState(id, transition) ConvocationDto
        }
        class IConvocationMapper {
            <<interface>>
            +toDomain(ConvocationDto) Convocation
            +toDto(Convocation) ConvocationDto
        }
        class ConvocationDto {
            +int id
            +String title
            +EnumConvocationType type
            +Date startDate
            +Date endDate
            +EnumConvocationState state
            +List convocationRequirements
        }
        class NotFoundException
        class ConflictException
    }

    namespace Domain {
        class Convocation {
            +validate(today, catalog)
            +close()
            +reopen(newEndDate, today)
            +cancel(reason)
        }
        class IConvocationRepository {
            <<interface>>
            +save(Convocation) Convocation
            +findAll() List~Convocation~
            +findById(id) Optional~Convocation~
            +deleteById(id)
        }
        class DomainValidationException {
            +Map errors
        }
    }

    namespace Infrastructure {
        class ConvocationRepository {
            -IConvocationCrudRepository crud
            -ConvocationPersistenceMapper mapper
        }
        class ConvocationPersistenceMapper {
            +toDomain(ConvocationEntity) Convocation
            +toEntity(Convocation) ConvocationEntity
        }
        class IConvocationCrudRepository {
            <<interface>>
            CrudRepository de Spring Data
        }
        class ConvocationEntity {
            <<@Entity>>
            +Integer id
            +Integer convocationTypeId
            +Integer stateConvocationId
        }
    }

    ConvocationController --> IConvocationService : usa
    ConvocationController ..> CustomResponse : responde
    GlobalExceptionHandler ..> DomainValidationException : traduce a HTTP
    GlobalExceptionHandler ..> NotFoundException : traduce a HTTP
    IConvocationService <|.. ConvocationService : implementa
    ConvocationService --> IConvocationRepository : usa
    ConvocationService --> IConvocationMapper : usa
    ConvocationService ..> Convocation : delega reglas
    IConvocationMapper ..> ConvocationDto
    IConvocationRepository <|.. ConvocationRepository : implementa
    ConvocationRepository --> IConvocationCrudRepository : usa
    ConvocationRepository --> ConvocationPersistenceMapper : usa
    ConvocationPersistenceMapper ..> ConvocationEntity
    ConvocationPersistenceMapper ..> Convocation
```

### Cómo leerlo

Empieza por las flechas de uso (`-->`). Todas van hacia adentro: el controlador usa la interfaz del servicio y el servicio usa la interfaz del repositorio. Ninguna clase de Domain usa algo de las otras capas.

Ahora busca la flecha de implementación (`<|..`) entre `ConvocationRepository` e `IConvocationRepository`. La interfaz está en Domain y la clase que la implementa está en Infrastructure, así que esta dependencia apunta hacia el dominio aunque la clase esté afuera. Eso es la inversión de dependencias: el dominio dice qué necesita y la infraestructura se adapta.

Fíjate también en que una convocatoria tiene tres formas, una por capa. `ConvocationDto` es lo que viaja en el JSON, `Convocation` tiene las reglas y `ConvocationEntity` es la fila de la tabla. Los mappers convierten entre ellas, y por eso un cambio en la tabla no obliga a tocar el dominio ni el JSON.

Con los errores pasa algo parecido. El dominio lanza `DomainValidationException` sin saber que existe HTTP, y `GlobalExceptionHandler` decide que eso es un 400.

## 2. Modelo de dominio

```mermaid
classDiagram
    direction LR

    class Convocation {
        -int id
        -String title
        -String description
        -EnumConvocationType type
        -LocalDate startDate
        -LocalDate endDate
        -EnumConvocationState state
        -String cancellationReason
        +validate(today, requirementCatalog)
        +validateChanges(originalStartDate, today, catalog)
        +ensureRequirementRulesUnchanged(currentRequirements)
        +applyDateRules(today)
        +close()
        +reopen(newEndDate, today)
        +finish(hasUndecidedRequests)
        +cancel(reason)
        +ensureCanReceiveRequests(today)
        +canDecideRequests() boolean
        +ensureCanDecideRequests()
    }
    class ConvocationRequirement {
        -int id
        -String name
        -String requiredValue
        -EnumConditionRequirement conditional
        -String description
        -int requirementId
        -int convocationId
        +validate(catalog, field, errors)
        +isMetBy(value) boolean
    }
    class Requirement {
        -int id
        -String name
        -boolean grade
    }
    class ConvocationType {
        -int id
        -String convocationTypeName
    }
    class Student {
        -int id
        -String name
        -String email
        -String program
        -double average
        -int stratum
    }
    class User {
        -int id
        -String email
        -String passwordHash
        -EnumRole role
        -Integer studentId
        +isStudent() boolean
    }
    class EnumRole {
        <<enumeration>>
        ADMIN
        STUDENT
    }
    class Request {
        -int id
        -int studentId
        -int convocationId
        -LocalDate creationDate
        -EnumStateRequest state
        -String comment
        +validateAgainst(convocationRequirements)
        +approve(convocation)
        +reject(convocation, comment)
        +cancel()
        +belongsTo(studentId) boolean
        +verifyRequirements(convocationRequirements) List
        +unmetRequirements(convocationRequirements) List
    }
    class RequestRequirement {
        -int id
        -int requestId
        -int convocationRequirementId
        -String value
        -String attachment
    }
    class EnumConvocationState {
        <<enumeration>>
        OPEN
        CLOSED
        FINISHED
        CANCELLED
    }
    class EnumConvocationType {
        <<enumeration>>
        MONITORING
        GRANT
        RESIDENCE
    }
    class EnumStateRequest {
        <<enumeration>>
        PENDING
        ACTIVE
        REJECT
        ACCEPT
        CANCELLED
    }
    class EnumConditionRequirement {
        <<enumeration>>
        EQUAL
        LESS_OR_EQUAL
        GREATER_OR_EQUAL
        LESS
        GREATER
    }
    class ConvocationState {
        <<interface>>
    }
    class RequestState {
        <<interface>>
    }

    Convocation "1" *-- "0..*" ConvocationRequirement : exige
    ConvocationRequirement "0..*" --> "1" Requirement : es del tipo
    Convocation "1" o-- "0..*" Request : recibe
    Student "1" o-- "0..*" Request : envía
    Request "1" *-- "1..*" RequestRequirement : responde
    RequestRequirement "0..*" --> "1" ConvocationRequirement : cumple
    Convocation --> EnumConvocationState
    Convocation --> EnumConvocationType
    Convocation ..> ConvocationState : delega según su estado
    Request --> EnumStateRequest
    Request ..> RequestState : delega según su estado
    ConvocationRequirement --> EnumConditionRequirement
    ConvocationType .. EnumConvocationType : mismo id
    User "0..1" --> "0..1" Student : inicia sesión como
    User --> EnumRole
```

### Qué es cada clase

| Clase | Qué representa | Reglas que tiene |
|---|---|---|
| `Convocation` | Una convocatoria de monitoría, beca o residencia. | Fechas y requisitos válidos, no cambiar los requisitos si ya hay solicitudes, y todo lo que depende del estado (lo resuelve el patrón State). |
| `ConvocationRequirement` | Un requisito concreto, por ejemplo "Promedio mayor o igual a 4.0". | El tipo debe existir y el valor ser mayor a 0; si es una nota, numérica y hasta 5.0. `isMetBy` compara el valor del estudiante con la condición: como número si los dos lo son, y si no, solo con `EQUAL` y como texto. |
| `Requirement` | El catálogo de tipos de requisito (Promedio, Estrato, Semestres...). | `grade` indica si el valor es una nota de 0 a 5. |
| `ConvocationType` | El catálogo de tipos de convocatoria guardado en la base. | Sus ids coinciden con `EnumConvocationType`. |
| `Student` | El estudiante que se postula. | Por ahora solo guarda datos. |
| `User` | Quien puede iniciar sesión: el administrador o un estudiante. | La contraseña se guarda solo como hash. Un `STUDENT` está ligado a su `Student` y el `ADMIN` no. |
| `Request` | La solicitud de un estudiante a una convocatoria. | Debe responder todos los requisitos, sin repetirlos ni agregar otros. Al crearla queda `ACTIVE` si cumple todo o `PENDING` si no; el sistema nunca la rechaza por su cuenta. |
| `RequestRequirement` | El valor que dio el estudiante para un requisito, con un adjunto opcional. | — |

Las entidades se relacionan por id (`convocationId`, `studentId`) y no guardan objetos anidados de JPA. Así el dominio no depende de cómo se cargan los datos y cada servicio decide qué consultar. Cómo queda esto en las tablas se ve en el [diagrama entidad-relación](diagrama-entidad-relacion.md).

## 3. Solicitudes: estados y eventos

La solicitud aplica el mismo patrón State que la convocatoria, en versión más simple. `PENDING` y `ACTIVE` se comportan igual porque las dos esperan al administrador, así que comparten la clase `ReviewRequestState`. Cada cambio de estado publica un evento por medio de un puerto de Application, y RabbitMQ queda escondido en Infrastructure.

```mermaid
classDiagram
    direction TB

    namespace Domain {
        class Request {
            -EnumStateRequest state
            -String comment
            +approve(convocation)
            +reject(convocation, comment)
            +cancel()
            +belongsTo(studentId) boolean
        }
        class RequestState {
            <<interface>>
            +value() EnumStateRequest
            +approve(request, convocation)
            +reject(request, convocation, comment)
            +cancel(request)
        }
        class RequestStates {
            <<fábrica>>
            +of(EnumStateRequest)$ RequestState
        }
        class BaseRequestState {
            <<abstract>>
            #label()* String
            #notAllowed(action) InvalidRequestStateException
        }
        class ReviewRequestState {
            PENDING y ACTIVE
            +approve() pasa a ACCEPT
            +reject() pasa a REJECT con justificación
            +cancel() pasa a CANCELLED
        }
        class AcceptedRequestState
        class RejectedRequestState
        class CancelledRequestState
        class InvalidRequestStateException
    }

    namespace Application {
        class RequestService {
            -IRequestRepository requestRepository
            -IRabbitMQSender eventSender
            +createRequest(dto, studentId) RequestDto
            +approveRequest(id) RequestDetailDto
            +rejectRequest(id, comment) RequestDetailDto
            +cancelRequest(id, studentId) StudentRequestDto
            -publishStateChange(...)
        }
        class IRabbitMQSender {
            <<interface>>
            +publishRequestStatusChanged(RequestStatusChangedEvent)
        }
        class RequestStatusChangedEvent {
            +String event = REQUEST_STATUS_CHANGED
            +int requestId
            +String studentName
            +String studentEmail
            +String convocationTitle
            +EnumStateRequest previousState
            +EnumStateRequest newState
            +String comment
            +String date
        }
    }

    namespace Infrastructure {
        class RabbitMQSender {
            -RabbitTemplate rabbitTemplate
            +publishRequestStatusChanged(event)
            -send(event) después del commit
        }
    }

    Request ..> RequestStates : of(state)
    RequestStates ..> RequestState : entrega
    RequestState <|.. BaseRequestState
    BaseRequestState <|-- ReviewRequestState
    BaseRequestState <|-- AcceptedRequestState
    BaseRequestState <|-- RejectedRequestState
    BaseRequestState <|-- CancelledRequestState
    BaseRequestState ..> InvalidRequestStateException : lanza
    RequestService ..> Request : delega reglas
    RequestService --> IRabbitMQSender : usa
    RequestService ..> RequestStatusChangedEvent : crea
    IRabbitMQSender <|.. RabbitMQSender : implementa
```

Aquí hay dos patrones más para identificar. `BaseRequestState` es un Template Method: responde *"No se puede aprobar una solicitud aprobada"* a cualquier acción que el estado no permita, y cada subclase solo aporta su nombre con `label()`. Los estados finales (aprobada, rechazada y retirada) no sobrescriben nada. `RabbitMQSender` es un adaptador: `RequestService` solo conoce `IRabbitMQSender`, y el envío real se deja para después del commit, así no sale un correo de un cambio que se deshizo.

## 4. Seguridad: login y JWT

`AuthService` no sabe nada de BCrypt, de JWT ni de Spring Security. Usa tres puertos y la infraestructura los implementa. El filtro y la configuración de Spring Security están en Api porque son la puerta de entrada HTTP.

```mermaid
classDiagram
    direction TB

    namespace Api {
        class AuthController {
            +login(LoginDto) CustomResponse
            +me(usuario del token) CustomResponse
        }
        class JwtAuthenticationFilter {
            -ITokenProvider tokenProvider
            #doFilterInternal(request, response, chain)
        }
        class SecurityConfig {
            rutas públicas, de ADMIN y de STUDENT
        }
        class SecurityErrorWriter {
            +write(response, status, message)
        }
    }

    namespace Application {
        class IAuthService {
            <<interface>>
            +login(LoginDto) SessionDto
            +me(AuthenticatedUserDto) SessionDto
        }
        class AuthService
        class IPasswordHasher {
            <<interface>>
            +hash(raw) String
            +matches(raw, hash) boolean
        }
        class ITokenProvider {
            <<interface>>
            +issue(AuthenticatedUserDto) IssuedToken
            +read(token) Optional~AuthenticatedUserDto~
        }
        class AuthenticatedUserDto {
            +int userId
            +String email
            +EnumRole role
            +Integer studentId
            +isAdmin() boolean
        }
        class UnauthorizedException
    }

    namespace Domain {
        class User
        class IUserRepository {
            <<interface>>
            +findByEmail(email) Optional~User~
        }
    }

    namespace Infrastructure {
        class UserRepository
        class BCryptPasswordHasher
        class JwtTokenProvider {
            HS256, vence en 8 horas
        }
    }

    AuthController --> IAuthService : usa
    IAuthService <|.. AuthService : implementa
    AuthService --> IUserRepository : usa
    AuthService --> IPasswordHasher : usa
    AuthService --> ITokenProvider : usa
    AuthService ..> UnauthorizedException : lanza
    IUserRepository ..> User
    JwtAuthenticationFilter --> ITokenProvider : read(token)
    JwtAuthenticationFilter ..> AuthenticatedUserDto : usuario de la petición
    SecurityConfig --> JwtAuthenticationFilter : registra
    SecurityConfig --> SecurityErrorWriter : 401 y 403
    IUserRepository <|.. UserRepository : implementa
    IPasswordHasher <|.. BCryptPasswordHasher : implementa
    ITokenProvider <|.. JwtTokenProvider : implementa
```

Una pregunta que suele salir: ¿por qué `IUserRepository` está en Domain y los otros dos puertos en Application? Porque `User` es una entidad del negocio, mientras que cifrar contraseñas y firmar tokens son herramientas técnicas que necesita el caso de uso. La idea de fondo es la misma en los tres: la capa de adentro define la interfaz y Infrastructure la implementa.

## 5. Patrón State de la convocatoria

Las reglas que cambian según el estado (qué se puede editar, cuándo se cierra, cuándo recibe solicitudes) no están en `if` dentro de los servicios. Cada estado es una clase (`OpenState`, `ClosedState`, `FinishedState` y `CancelledState`) que hereda de `BaseConvocationState`, y `Convocation` le pregunta a su estado actual qué hacer. El diagrama y la explicación completa están en [estados-convocatoria.md](estados-convocatoria.md#implementación-patrón-state).

## Otros diagramas

- [Secuencia por capas](diagrama-secuencia.md): los seis casos de uso con la capa Domain separada.
- [Estados de la convocatoria](estados-convocatoria.md): ciclo de vida y patrón State.
- [Entidad-relación](diagrama-entidad-relacion.md): las tablas de SQL Server.
- [Despliegue](diagrama-despliegue.md): los contenedores del Docker Compose.
