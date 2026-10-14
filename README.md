<h1 align="center">Sistema de Convocatorias · Clean Architecture</h1>

<p align="center">
  Plataforma de convocatorias y solicitudes de la <b>Universidad de Caldas</b>, construida en el <b>Semillero SOLID</b><br/>
  como ejemplo práctico de Clean Architecture, patrones de diseño y arquitectura orientada a eventos.
</p>

<p align="center">
  <img src="https://skillicons.dev/icons?i=java,spring,hibernate,gradle,docker,rabbitmq,react,vite,js,nodejs,ts,py" alt="Tecnologías del proyecto" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.2.5" />
  <img src="https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate JPA" />
  <img src="https://img.shields.io/badge/SQL%20Server-2022-CC2927?style=for-the-badge&logo=data:image/svg%2bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0id2hpdGUiPjxwYXRoIGQ9Ik0xMiAyQzcuNiAyIDQgMy4zIDQgNXYxNGMwIDEuNyAzLjYgMyA4IDNzOC0xLjMgOC0zVjVjMC0xLjctMy42LTMtOC0zem0wIDJjMy45IDAgNiAxLjEgNiAxcy0yLjEgMS02IDEtNi0xLjEtNi0xIDIuMS0xIDYtMXpNNiA3LjRDNy41IDguMiA5LjYgOC41IDEyIDguNXM0LjUtLjMgNi0xLjFWMTFjMCAuNC0yLjEgMS41LTYgMS41UzYgMTEuNCA2IDExem0wIDZjMS41LjggMy42IDEuMSA2IDEuMXM0LjUtLjMgNi0xLjFWMTdjMCAuNC0yLjEgMS41LTYgMS41UzYgMTcuNCA2IDE3eiIvPjwvc3ZnPg==" alt="SQL Server 2022" />
  <img src="https://img.shields.io/badge/RabbitMQ-3.8-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ 3.8" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Compose" />
  <img src="https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/Gradle-wrapper-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" />
</p>

<p align="center">
  <a href="#visión-general">Visión general</a> ·
  <a href="#clean-architecture-en-este-proyecto">Clean Architecture</a> ·
  <a href="#ciclo-de-vida-de-una-convocatoria">Estados</a> ·
  <a href="#endpoints">Endpoints</a> ·
  <a href="#cómo-ejecutar-todo-el-sistema">Ejecutar</a> ·
  <a href="#diagramas">Diagramas</a>
</p>

---

Este repositorio es el **backend** y también la **documentación principal** del sistema completo. Los otros dos repositorios tienen su propio README con el detalle interno de cada uno:

| Repositorio | Rol | Documentación |
|---|---|---|
| **clean-architecture-api** (este) | API REST con las reglas del negocio | Este documento |
| [clean-architecture-front-main](https://github.com/SemilleroSOLID/clean-architecture-front-main) | Aplicación web para administradores y estudiantes | [README del front](https://github.com/SemilleroSOLID/clean-architecture-front-main#readme) |
| [clean-architecture-notification](https://github.com/SemilleroSOLID/clean-architecture-notification) | Microservicio de notificaciones por eventos | [README de notificaciones](https://github.com/SemilleroSOLID/clean-architecture-notification#readme) |

## Contenido

1. [Visión general](#visión-general)
2. [Tecnologías](#tecnologías)
3. [Clean Architecture en este proyecto](#clean-architecture-en-este-proyecto)
4. [Las cuatro capas](#las-cuatro-capas)
5. [Cómo se cumple la regla de dependencias](#cómo-se-cumple-la-regla-de-dependencias)
6. [Ciclo de vida de una convocatoria](#ciclo-de-vida-de-una-convocatoria)
7. [Recorrido de una petición](#recorrido-de-una-petición)
8. [Patrones de diseño](#patrones-de-diseño)
9. [Estructura de carpetas](#estructura-de-carpetas)
10. [Cómo agregar una funcionalidad](#cómo-agregar-una-funcionalidad)
11. [Endpoints](#endpoints)
12. [Cómo ejecutar todo el sistema](#cómo-ejecutar-todo-el-sistema)
13. [Pruebas](#pruebas)
14. [Diagramas](#diagramas)
15. [Estado del proyecto](#estado-del-proyecto)

## Visión general

El sistema resuelve dos casos de uso de la universidad:

- **CU-001 · Gestionar convocatorias.** El administrador publica convocatorias de monitoría, beca de investigación o residencia estudiantil, cada una con fechas y requisitos (por ejemplo, *promedio mayor o igual a 4.0*). Después las edita, las cierra, las reabre, las finaliza o las cancela.
- **CU-002 · Postularse.** El estudiante envía una solicitud a una convocatoria abierta, entregando un valor para cada requisito. El administrador las revisa y decide.

Está dividido en tres aplicaciones independientes que se comunican por HTTP y por mensajes:

```mermaid
flowchart LR
    U([Administrador<br/>Estudiante]) --> F

    subgraph front["clean-architecture-front-main"]
        F["React 18 + Vite<br/>Ant Design"]
    end

    subgraph api["clean-architecture-api"]
        direction TB
        A1[Api] --> A2[Application] --> A3[Domain]
        A4[Infrastructure] -.-> A3
    end

    subgraph notif["clean-architecture-notification"]
        C["Consumidores<br/>Node.js · Python"]
    end

    F -- "HTTP / JSON :3001" --> A1
    A4 -- "JDBC :1435" --> DB[(SQL Server)]
    A4 -- "publica :5673" --> MQ{{RabbitMQ}}
    MQ -- "cola event.drivent.email" --> C
    C -. "correo (planeado #21)" .-> U
```

| Comunicación | Tipo | Por qué |
|---|---|---|
| Front → API | **Síncrona** (HTTP/REST) | El usuario necesita la respuesta en el momento: si la convocatoria se guardó o qué campo tiene error. |
| API → Notificaciones | **Asíncrona** (RabbitMQ) | Enviar un correo puede tardar o fallar. El backend publica el evento y responde de inmediato; si el consumidor está caído, el mensaje espera en la cola. |

## Tecnologías

| Repositorio | Tecnologías |
|---|---|
| **API** | <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" /> <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white" /> <img src="https://img.shields.io/badge/Spring%20Data%20JPA-Hibernate-59666C?logo=hibernate&logoColor=white" /> <img src="https://img.shields.io/badge/Bean%20Validation-Jakarta-6DB33F?logo=spring&logoColor=white" /> <img src="https://img.shields.io/badge/Spring%20AMQP-RabbitMQ-FF6600?logo=rabbitmq&logoColor=white" /> <img src="https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white" /> <img src="https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white" /> |
| **Front** | <img src="https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black" /> <img src="https://img.shields.io/badge/Vite-8-646CFF?logo=vite&logoColor=white" /> <img src="https://img.shields.io/badge/Ant%20Design-6-0170FE?logo=antdesign&logoColor=white" /> <img src="https://img.shields.io/badge/React%20Router-7-CA4245?logo=reactrouter&logoColor=white" /> <img src="https://img.shields.io/badge/Axios-1-5A29E4?logo=axios&logoColor=white" /> <img src="https://img.shields.io/badge/JavaScript-ES2022-F7DF1E?logo=javascript&logoColor=black" /> |
| **Notificaciones** | <img src="https://img.shields.io/badge/Node.js-20-5FA04E?logo=nodedotjs&logoColor=white" /> <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white" /> <img src="https://img.shields.io/badge/Python-3.11-3776AB?logo=python&logoColor=white" /> <img src="https://img.shields.io/badge/Pydantic-2-E92063?logo=pydantic&logoColor=white" /> <img src="https://img.shields.io/badge/RabbitMQ-3.8-FF6600?logo=rabbitmq&logoColor=white" /> |
| **Infraestructura** | <img src="https://img.shields.io/badge/SQL%20Server-2022-CC2927" /> <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white" /> <img src="https://img.shields.io/badge/Mermaid-diagramas-FF3670?logo=mermaid&logoColor=white" /> |

Dentro del backend, cada tecnología tiene un lugar fijo, y eso es parte de la arquitectura:

| Tecnología | Para qué | Capa donde se usa |
|---|---|---|
| Spring Web | Controladores REST | Api |
| Jakarta Bean Validation | Validar formato del JSON (`@NotBlank`, `@NotNull`, `@Size`) | Application (DTOs) |
| Spring Data JPA / Hibernate | Leer y escribir en SQL Server | Solo Infrastructure |
| Spring AMQP | Publicar mensajes en RabbitMQ | Solo Infrastructure |
| JUnit 5 | Pruebas unitarias | Domain (sin Spring) |
| Java puro (`java.time`, colecciones) | Reglas del negocio | Domain |

## Clean Architecture en este proyecto

Clean Architecture (Robert C. Martin, *Clean Architecture*, 2017) separa **lo que hace el negocio** de **cómo se entrega y se guarda**. Las reglas de una convocatoria ("la fecha de fin debe ser al menos un día después de la de inicio", "una convocatoria cerrada no se edita") no deberían cambiar porque se cambie SQL Server por PostgreSQL o REST por GraphQL.

El código se divide en capas concéntricas con una sola regla:

> **Regla de dependencias:** el código solo puede depender de capas más internas. Lo de adentro no sabe que existe lo de afuera.

```mermaid
flowchart TB
    subgraph L4["Api · Infrastructure  —  detalles: web, base de datos, mensajería"]
        subgraph L2["Application  —  casos de uso"]
            subgraph L1["Domain  —  reglas del negocio"]
                D["Entidades · Enums · Estados<br/>Excepciones · Interfaces de repositorio"]
            end
        end
    end
    style L1 fill:#fff3c4,stroke:#c9a400
    style L2 fill:#dbeafe,stroke:#3b82f6
    style L4 fill:#e5e7eb,stroke:#6b7280
```

Lo que se gana:

- **El negocio se prueba sin base de datos ni servidor.** Las 37 pruebas del dominio corren en milisegundos con JUnit, sin levantar Spring.
- **Los cambios de tecnología quedan aislados.** Cambiar la base de datos implica reescribir Infrastructure; el dominio y los casos de uso no se tocan.
- **Cada regla vive en un solo lugar.** Para cambiar cuándo se cierra una convocatoria se abre `OpenState.java`, no hay condicionales repartidos por los servicios.

## Las cuatro capas

El código está en `src/main/java/com/example/demo/` y cada carpeta de primer nivel es una capa.

```mermaid
flowchart LR
    Api["<b>Api</b><br/>Controladores<br/>Manejo de errores<br/>Configuración"]
    App["<b>Application</b><br/>Servicios (casos de uso)<br/>DTOs y mappers<br/>Puertos de salida"]
    Dom["<b>Domain</b><br/>Entidades y reglas<br/>Patrón State<br/>Interfaces de repositorio"]
    Inf["<b>Infrastructure</b><br/>Entidades JPA<br/>Repositorios<br/>RabbitMQ"]

    Api -->|usa interfaces de servicio| App
    App -->|usa entidades e interfaces| Dom
    Inf -.->|implementa repositorios| Dom
    Inf -.->|implementa IRabbitMQSender| App
```

### Domain · el núcleo

No depende de nada: ni Spring, ni JPA, ni Jackson. Solo Java.

| Contiene | Ejemplo |
|---|---|
| Entidades con reglas | `Convocation.validate(today, catalog)` revisa fechas y requisitos. `Request.validateAgainst(requirements)` revisa que la solicitud responda todos los requisitos. |
| Enums del negocio | `EnumConvocationState`, `EnumStateRequest`, `EnumConvocationType`, `EnumConditionRequirement`. |
| Patrón State | `Domain/States/`, una clase por estado de la convocatoria. |
| Excepciones de negocio | `DomainValidationException` (mapa campo → mensaje) e `InvalidConvocationStateException`. |
| Puertos de persistencia | `IConvocationRepository`, `IRequestRepository`... Dicen **qué** se necesita guardar o consultar, no **cómo**. |

La fecha de "hoy" no se obtiene dentro del dominio con `LocalDate.now()`: se recibe como parámetro. Así las pruebas fijan el día que necesitan y el dominio no depende del reloj del servidor.

### Application · los casos de uso

Coordina. Un servicio carga lo que necesita mediante las interfaces del dominio, le pide a las entidades que validen o cambien de estado y guarda el resultado. **No contiene reglas del negocio.**

| Contiene | Ejemplo |
|---|---|
| Interfaces de servicio | `IConvocationService`, `IRequestService`, `IStudentService`. Los controladores dependen de estas. |
| Servicios | `ConvocationService.closeConvocation(id)`: carga, llama a `convocation.close()`, guarda. |
| DTOs | `ConvocationDto`, `RequestDto`. Contrato con el front, con las anotaciones de Bean Validation. |
| Mappers DTO ↔ dominio | `ConvocationMapper`, `RequestMapper`, `StudentMapper`. |
| Excepciones de aplicación | `NotFoundException`, `ConflictException`. |
| Puertos de salida | `IRabbitMQSender`: el servicio sabe que puede enviar un mensaje, no que es RabbitMQ. |

### Infrastructure · los detalles técnicos

| Contiene | Ejemplo |
|---|---|
| Entidades JPA | `ConvocationEntity` con `@Entity` y `@Column`. Representan tablas, **no** el negocio. |
| CRUDs de Spring Data | `IConvocationCrudRepository extends CrudRepository<ConvocationEntity, Integer>`. |
| Adaptadores de repositorio | `ConvocationRepository implements IConvocationRepository`. |
| Mappers de persistencia | `ConvocationPersistenceMapper`: `ConvocationEntity` ↔ `Convocation`. |
| Mensajería | `RabbitMQSender implements IRabbitMQSender` y la topología en `RabbitMQService`. |

### Api · la puerta de entrada

| Contiene | Ejemplo |
|---|---|
| Controladores | `ConvocationController`, `RequestController`, `StudentController`, `RequirementsController`. Un método = una línea que llama al servicio. |
| Respuesta común | `CustomResponse<T>` con `data`, `codeState` y `message`. |
| Errores | `GlobalExceptionHandler`: dominio inválido → 400, no existe → 404, conflicto → 409. |
| Configuración | `ClockConfig` (zona `America/Bogota`), `JacksonConfig`, `WebConfig` (CORS). |

## Cómo se cumple la regla de dependencias

Se puede comprobar en el código. Estas tres búsquedas **no devuelven ningún resultado**:

```bash
# Domain no importa nada de otras capas ni de frameworks
grep -rn "^import" src/main/java/com/example/demo/Domain | grep -vE "com.example.demo.Domain|java\."

# Application no conoce Infrastructure ni Api
grep -rn "import com.example.demo.\(Infrastructure\|Api\)" src/main/java/com/example/demo/Application

# Api no conoce Infrastructure
grep -rn "import com.example.demo.Infrastructure" src/main/java/com/example/demo/Api
```

| Principio | Cómo está aplicado |
|---|---|
| **Inversión de dependencias** | El dominio define `IConvocationRepository`; Infrastructure la implementa con JPA; Spring inyecta la implementación en el servicio, que solo conoce la interfaz. |
| **Negocio separado de las tablas** | `Convocation` (dominio) y `ConvocationEntity` (JPA) son clases distintas. El dominio usa el enum `OPEN`; la tabla guarda `stateConvocationId = 1`. El mapper traduce. |
| **Negocio separado del JSON** | El controlador recibe un `ConvocationDto`. Si el front pide un campo nuevo, cambian el DTO y el mapper, no la entidad. |
| **Reglas dentro de las entidades** | Los servicios no validan fechas ni preguntan por el estado: llaman a `convocation.validate(...)`, `convocation.close()`, `request.validateAgainst(...)`. |
| **Errores sin HTTP en el dominio** | El dominio lanza `DomainValidationException`; solo `GlobalExceptionHandler` sabe que eso es un 400. |

> **Concesión consciente.** Application usa anotaciones de Spring (`@Service`, `@Autowired`, `@Transactional`). En una versión estricta esa capa tampoco dependería del framework. Se acepta porque simplifica el proyecto y no afecta lo esencial: **el dominio es Java puro** y se prueba sin Spring.

## Ciclo de vida de una convocatoria

Es la parte central de lo implementado. Una convocatoria pasa por cuatro estados y cada uno permite acciones distintas. Las reglas se definieron en [#30](https://github.com/SemilleroSOLID/clean-architecture-api/issues/30) y se implementaron con el **patrón State** en [#31](https://github.com/SemilleroSOLID/clean-architecture-api/issues/31).

```mermaid
stateDiagram-v2
    direction LR
    [*] --> Abierta : crear
    Abierta --> Cerrada : cerrar (administrador)<br/>o pasa la fecha de fin (automático)
    Cerrada --> Abierta : reabrir<br/>[nueva fecha de fin posterior a hoy]
    Cerrada --> Finalizada : finalizar<br/>[ninguna solicitud sin decidir]
    Abierta --> Cancelada : cancelar [con motivo]
    Cerrada --> Cancelada : cancelar [con motivo]
    Abierta --> [*] : eliminar [sin solicitudes]
    Finalizada --> [*]
    Cancelada --> [*]

    note right of Abierta
        Recibe solicitudes solo entre
        la fecha de inicio y la de fin.
        Se puede editar.
    end note
    note right of Cerrada
        Aquí se aprueban o rechazan
        las solicitudes.
    end note
```

### Qué significa cada estado

| Estado | Enum | Qué pasa en él | Color en el front |
|---|---|---|---|
| **Abierta** | `OPEN` | Estado inicial al crearla. Recibe solicitudes mientras hoy esté entre la fecha de inicio y la de fin. Se puede editar, cerrar, cancelar y eliminar (si no tiene solicitudes). | 🟢 verde |
| **Cerrada** | `CLOSED` | Ya no recibe solicitudes. El administrador decide las que llegaron. Se puede reabrir con una nueva fecha de fin, finalizar o cancelar. | 🟠 naranja |
| **Finalizada** | `FINISHED` | Todas las solicitudes fueron decididas. Solo consulta. | 🔵 azul |
| **Cancelada** | `CANCELLED` | Se suspendió con un motivo obligatorio. Reemplaza la eliminación cuando ya hay solicitudes. Solo consulta. | 🔴 rojo |

### Acciones permitidas

| Acción | Endpoint | Abierta | Cerrada | Finalizada | Cancelada |
|---|---|:-:|:-:|:-:|:-:|
| Editar | `PUT /updateConvocation/{id}` | ✅ | ❌ | ❌ | ❌ |
| Eliminar | `DELETE /deleteConvocation/{id}` | ✅ sin solicitudes | ❌ | ❌ | ❌ |
| Cerrar | `PUT /closeConvocation/{id}` | ✅ | ❌ | ❌ | ❌ |
| Reabrir | `PUT /reopenConvocation/{id}` | ❌ | ✅ | ❌ | ❌ |
| Finalizar | `PUT /finishConvocation/{id}` | ❌ | ✅ si todo está decidido | ❌ | ❌ |
| Cancelar | `PUT /cancelConvocation/{id}` | ✅ | ✅ | ❌ | ❌ |
| Recibir solicitudes | `POST /request/createRequest` | ✅ dentro de las fechas | ❌ | ❌ | ❌ |
| Decidir solicitudes | (planeado, #17) | ❌ | ✅ | ❌ | ❌ |

Si se intenta una acción no permitida, la API responde **400** con un mensaje claro, por ejemplo `{ "state": "No se puede editar una convocatoria cerrada" }`. El front muestra solo los botones permitidos en cada estado, pero la regla real está en el backend.

### Cierre automático

No hay una tarea programada que cierre convocatorias. Cada vez que se consulta o se usa una convocatoria, el servicio llama a `convocation.applyDateRules(hoy)`: si está **Abierta** y la fecha de fin ya pasó, el propio estado la pasa a **Cerrada**. Así la regla está en el dominio y no depende de que un proceso externo se ejecute a tiempo. "Hoy" se calcula en la zona horaria de Colombia, no en la del contenedor (UTC).

### Cómo está implementado: patrón State

Sin el patrón, cada servicio tendría condicionales como `if (state == OPEN) ... else if (state == CLOSED) ...` repetidos en cada acción. Con el patrón, **cada estado es una clase** que sabe qué permite y a qué estado pasa.

```mermaid
classDiagram
    direction LR
    class Convocation {
        -EnumConvocationState state
        +close()
        +reopen(newEndDate, today)
        +finish(hasUndecidedRequests)
        +cancel(reason)
        +applyDateRules(today)
        +ensureCanBeEdited()
        +ensureCanReceiveRequests(today)
    }
    class ConvocationState {
        <<interface>>
        +close(convocation)
        +reopen(convocation, newEndDate, today)
        +finish(convocation, hasUndecided)
        +cancel(convocation, reason)
        +applyDateRules(convocation, today)
        +ensureCanBeEdited(convocation)
        +ensureCanReceiveRequests(convocation, today)
    }
    class BaseConvocationState {
        <<abstract>>
        rechaza todo por defecto
        +notAllowed(acción) excepción 400
    }
    class OpenState {
        editar, eliminar, cerrar, cancelar
        cierre automático por fecha
        recibe solicitudes en fechas
    }
    class ClosedState {
        reabrir, finalizar, cancelar
        decide solicitudes
    }
    class FinishedState {
        solo consulta
    }
    class CancelledState {
        solo consulta
    }
    class ConvocationStates {
        +of(EnumConvocationState)$ ConvocationState
    }

    Convocation ..> ConvocationStates : pide su estado
    ConvocationStates ..> ConvocationState : devuelve
    ConvocationState <|.. BaseConvocationState
    BaseConvocationState <|-- OpenState
    BaseConvocationState <|-- ClosedState
    BaseConvocationState <|-- FinishedState
    BaseConvocationState <|-- CancelledState
```

Cómo funciona, paso a paso:

1. En la base de datos solo se guarda el número del estado (`stateConvocationId`). El mapper lo convierte en el enum `EnumConvocationState`.
2. Cuando se llama, por ejemplo, `convocation.close()`, la entidad le pide a `ConvocationStates.of(state)` el objeto de su estado actual y le delega la acción.
3. `BaseConvocationState` **rechaza todo por defecto**. Cada estado concreto sobrescribe solo lo que permite. Por eso `FinishedState` y `CancelledState` casi no tienen código: heredan el rechazo.
4. Si la acción es válida, el estado cambia el enum de la convocatoria (por ejemplo a `CLOSED`) y el servicio la guarda.

```java
// ConvocationService: no hay ningún if sobre el estado
public ConvocationDto closeConvocation(int convocationId) {
    return this.changeState(convocationId, Convocation::close);
}

// OpenState: solo una convocatoria abierta se puede cerrar
@Override
public void close(Convocation convocation) {
    convocation.setState(EnumConvocationState.CLOSED);
}
```

Agregar un estado nuevo (por ejemplo *Suspendida*) es crear su clase y registrarla en `ConvocationStates`, sin tocar los servicios. Es el principio abierto/cerrado de SOLID. Todas las transiciones válidas e inválidas están cubiertas por `ConvocationStateTest` (21 pruebas).

El detalle completo, con las decisiones del equipo y el anexo de estados de la solicitud, está en [diagramas/estados-convocatoria.md](diagramas/estados-convocatoria.md).

## Recorrido de una petición

Ejemplo: el administrador cierra la convocatoria 5 con `PUT /convocation/closeConvocation/5`.

1. **Api.** `ConvocationController.closeConvocation(5)` llama a `IConvocationService.closeConvocation(5)`.
2. **Application.** `ConvocationService` busca la convocatoria con `IConvocationRepository.findById(5)`.
3. **Infrastructure.** `ConvocationRepository` consulta SQL Server, recibe un `ConvocationEntity` y lo convierte en `Convocation`.
4. **Domain.** `applyDateRules(hoy)` aplica el cierre automático y luego `close()` delega en el estado actual. `OpenState` la pasa a `CLOSED`; cualquier otro estado lanza *"No se puede cerrar una convocatoria cerrada"*.
5. **Infrastructure.** El servicio guarda con `IConvocationRepository.save(...)`.
6. **Api.** El controlador responde `200` con un `CustomResponse`. Si hubo una excepción, `GlobalExceptionHandler` responde 400, 404 o 409.

Los recorridos completos, con los casos de error, están en [diagramas/diagrama-secuencia.md](diagramas/diagrama-secuencia.md).

## Patrones de diseño

| Patrón | Dónde | Para qué |
|---|---|---|
| **Repository** | `Domain/Interfaces` + `Infrastructure/Persistence/Repositories` | Esconder cómo se guardan los datos detrás de una interfaz del dominio. |
| **Adapter** | `ConvocationRepository`, `RabbitMQSender` | Adaptar una tecnología a la interfaz que pide el núcleo. |
| **DTO** | `Application/Dtos` | Separar el contrato JSON de las entidades. |
| **Mapper** | `Application/Mappers`, `Infrastructure/Mappers` | Convertir entre DTO, dominio y entidad JPA, cada uno en su capa. |
| **State** | `Domain/States` | Reglas de cada estado de la convocatoria en su propia clase. |
| **Simple Factory** | `ConvocationStates.of(enum)` | Obtener el objeto de estado a partir del enum guardado. |
| **Dependency Injection** | Todo el backend | Spring entrega las implementaciones de las interfaces. |
| **Publish / Subscribe** | RabbitMQ + notificaciones | Desacoplar el envío de notificaciones del backend. |

## Estructura de carpetas

```text
clean-architecture-api/
├── src/main/java/com/example/demo/
│   ├── Api/                          ← entrada HTTP
│   │   ├── Config/                   ← ClockConfig, JacksonConfig, WebConfig, GlobalExceptionHandler
│   │   ├── Controllers/              ← Convocation, Request, Student, Requirements, Test
│   │   └── Dtos/                     ← CustomResponse
│   ├── Application/                  ← casos de uso
│   │   ├── I*Service.java            ← interfaces que usan los controladores
│   │   ├── IRabbitMQSender.java      ← puerto de mensajería
│   │   ├── Dtos/                     ← ConvocationDto, RequestDto, StudentDto...
│   │   ├── Exceptions/               ← NotFoundException, ConflictException
│   │   ├── Mappers/                  ← DTO ↔ dominio
│   │   └── Services/                 ← ConvocationService, RequestService...
│   ├── Domain/                       ← reglas del negocio (Java puro)
│   │   ├── Entities/                 ← Convocation, Request, Student, ConvocationRequirement...
│   │   ├── Enums/                    ← estados, tipos y condiciones
│   │   ├── Exceptions/               ← DomainValidationException, InvalidConvocationStateException
│   │   ├── Interfaces/               ← puertos de persistencia
│   │   └── States/                   ← OpenState, ClosedState, FinishedState, CancelledState
│   └── Infrastructure/               ← detalles técnicos
│       ├── Implementations/Services/ ← RabbitMQSender, RabbitMQService
│       ├── Mappers/                  ← entidad JPA ↔ dominio
│       └── Persistence/
│           ├── Cruds/                ← interfaces de Spring Data
│           ├── Entities/             ← clases @Entity
│           └── Repositories/         ← implementaciones de los puertos
├── src/test/java/.../Domain/         ← pruebas unitarias del dominio
├── diagramas/                        ← diagramas Mermaid (estados, clases, secuencia)
├── create-database.sql               ← base de datos y datos de ejemplo
├── docker-compose.yml                ← SQL Server + backend
├── Dockerfile                        ← imagen del backend
└── sqlserver.Dockerfile              ← SQL Server con el script inicial
```

## Cómo agregar una funcionalidad

Ejemplo: *"un estudiante puede retirar su solicitud"*. Se trabaja de adentro hacia afuera, para diseñar el dominio sin pensar en la base de datos:

1. **Domain.** Método `withdraw()` en `Request` con su regla (solo si no está decidida). Si hace falta una consulta nueva, se agrega a `IRequestRepository`. Prueba unitaria en `src/test/.../Domain`.
2. **Application.** `withdrawRequest(id)` en `IRequestService` y `RequestService`: cargar, llamar a `request.withdraw()`, guardar.
3. **Infrastructure.** Implementar el método nuevo del repositorio, si lo hubo.
4. **Api.** Endpoint en `RequestController` que solo llama al servicio.

Antes de subir el cambio se corren las tres búsquedas de [la regla de dependencias](#cómo-se-cumple-la-regla-de-dependencias). Si alguna devuelve algo, hay una dependencia en la dirección equivocada.

## Endpoints

Base: `http://localhost:3001/ms-logica-negocio/api`. Todas las respuestas tienen la forma `{ "data": ..., "codeState": ..., "message": "..." }`.

<details open>
<summary><b>Convocatorias</b></summary>

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/convocation/getAllConvocations` | Lista todas con su estado actual. |
| GET | `/convocation/getConvocationById/{id}` | Detalle con requisitos. |
| GET | `/convocation/getAllConvocationTypes` | Tipos de convocatoria. |
| POST | `/convocation/createConvocation` | Crea una convocatoria (queda `OPEN`). |
| PUT | `/convocation/updateConvocation/{id}` | Edita una abierta. |
| DELETE | `/convocation/deleteConvocation/{id}` | Elimina una abierta sin solicitudes. |
| PUT | `/convocation/closeConvocation/{id}` | Cierra. |
| PUT | `/convocation/reopenConvocation/{id}` | Reabre. Cuerpo: `{ "endDate": "AAAA-MM-DD" }`. |
| PUT | `/convocation/finishConvocation/{id}` | Finaliza. |
| PUT | `/convocation/cancelConvocation/{id}` | Cancela. Cuerpo: `{ "reason": "..." }`. |

</details>

<details open>
<summary><b>Requisitos, estudiantes y solicitudes</b></summary>

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/requirements/getAllRequirements` | Catálogo de tipos de requisito. |
| GET | `/requirements/getAllConditionRequirements` | Condiciones (igual, mayor o igual...). |
| GET | `/student/getAllStudents` | Lista de estudiantes. |
| GET | `/student/getStudentById/{id}` | Detalle de un estudiante. |
| POST | `/request/createRequest` | Crea la solicitud de un estudiante. |
| GET | `/test/hello` | Comprueba que el servidor responde. |

</details>

| Código | Cuándo |
|---|---|
| `400` | Datos inválidos o acción no permitida en el estado actual. El cuerpo trae un mapa `campo → mensaje`. |
| `404` | La convocatoria, el estudiante o el recurso no existe. |
| `409` | Conflicto, por ejemplo una solicitud repetida o eliminar una convocatoria con solicitudes. |

## Cómo ejecutar todo el sistema

### Requisitos

- Docker y Docker Compose.
- Node.js 22 para el front y Node.js 20 para el consumidor de notificaciones.
- Java 17 solo si se quiere compilar o correr las pruebas fuera de Docker.
- Los tres repositorios clonados en la misma carpeta.

### Pasos

```bash
# 1. RabbitMQ (repositorio de notificaciones)
cd clean-architecture-notification/node
docker compose up -d

# 2. SQL Server + backend (este repositorio)
cd ../../clean-architecture-api
docker compose up -d --build

# 3. Front
cd ../clean-architecture-front-main
npm install
cp .env.example .env
npm run dev

# 4. (Opcional) Consumidor de notificaciones
cd ../clean-architecture-notification/node
npm install
cp .env.example .env      # descomentar AMQP_URL
npm run start:consumer -- email
```

El contenedor `sqlserver` ejecuta `create-database.sql` al iniciar: crea `CleanArchitectureDB` y carga los requisitos, los tipos de convocatoria y cinco estudiantes de ejemplo. El backend espera a que la base de datos esté lista.

| Servicio | Dirección |
|---|---|
| Front | http://localhost:5173 |
| Backend | http://localhost:3001/ms-logica-negocio/api |
| SQL Server | `localhost:1435` (usuario `sa`, contraseña en `sqlserver.Dockerfile`) |
| RabbitMQ | `localhost:5673` · panel en http://localhost:15673 (`admin` / `admin`) |

### Configuración del backend

`src/main/resources/application-dev.properties` lee estas variables:

| Variable | Valor por defecto |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `1433` |
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_PORT` | `5673` |

Dentro de Docker Compose el backend usa `sqlserver:1433` y llega a RabbitMQ por `host.docker.internal:5673`. La zona horaria de las reglas es `America/Bogota` (propiedad `app.time-zone`).

## Pruebas

```bash
./gradlew test
```

| Clase | Pruebas | Qué cubre |
|---|:-:|---|
| `ConvocationValidationTest` | 10 | Reglas de fechas y requisitos al crear y editar. |
| `ConvocationStateTest` | 21 | Cada transición válida e inválida de los cuatro estados, el cierre automático y la regla de no cambiar requisitos con solicitudes. |
| `RequestValidationTest` | 6 | Que una solicitud responda todos los requisitos, sin repetidos ni ajenos. |

Las pruebas crean los objetos con `new`, sin Spring ni base de datos. Que se pueda probar así es una de las señales más claras de que el dominio está bien aislado.

## Diagramas

| Diagrama | Qué muestra |
|---|---|
| [Estados de la convocatoria](diagramas/estados-convocatoria.md) | Ciclo de vida, acciones por estado, decisiones del equipo y el patrón State. |
| [Clases](diagramas/diagrama-clases.md) | Las clases de la convocatoria en las cuatro capas y el modelo de dominio. |
| [Secuencia](diagramas/diagrama-secuencia.md) | Crear convocatoria, cambiar estado, crear solicitud y el flujo planeado de aprobar y notificar. |

Están escritos en [Mermaid](https://mermaid.js.org/): GitHub los dibuja directamente y se editan como texto.

## Estado del proyecto

El trabajo se organiza en [issues](https://github.com/SemilleroSOLID/clean-architecture-api/issues) por fases.

| Fase | Contenido | Estado |
|---|---|---|
| 1 · Convocatorias (CU-001) | Crear, consultar, editar, eliminar y listar; validaciones; refactor a Clean Architecture; diagrama de estados. | ✅ Terminada |
| 2 · Solicitudes (CU-002) | Estudiantes y creación de solicitudes listos. Faltan la verificación automática de requisitos (#14) y el formulario del front (#15). | 🔄 En curso |
| 3 · Gestión y notificaciones | Ver, aprobar y rechazar solicitudes, historial y retiro del estudiante, eventos en RabbitMQ, correo real. | ⏳ Pendiente |
| 4 · Autenticación y roles | JWT con roles Administrador y Estudiante, login y rutas protegidas. | ⏳ Pendiente |
| 5 · Calidad y despliegue | Patrón State listo. Faltan pruebas con Mockito, pruebas de integración, GitHub Actions, Docker Compose único y diagramas de despliegue y entidad-relación. | 🔄 En curso |

Al terminar el backlog la estructura de capas será la misma; crece el contenido de cada una:

- **Domain:** verificación de requisitos y estados de la solicitud (pendiente, activa, aprobada, rechazada, retirada), probablemente con el mismo patrón State.
- **Application:** aprobar, rechazar y retirar, y un puerto para publicar eventos de notificación.
- **Infrastructure:** ese puerto implementado con RabbitMQ y la parte técnica del JWT.
- **Api:** endpoints nuevos protegidos por rol. El dominio no sabrá quién está autenticado: recibe el id del usuario como un dato más.

---

<p align="center">Semillero SOLID · Universidad de Caldas</p>
