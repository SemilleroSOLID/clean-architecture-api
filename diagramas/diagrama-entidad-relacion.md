# Diagrama entidad-relación

Las tablas de `CleanArchitectureDB` tal como quedan en SQL Server. Salen de dos lugares:

- **[`create-database.sql`](../create-database.sql)** crea los catálogos y los datos de prueba: `Requirement`, `Student`, `ConvocationType` y `Users`.
- **Hibernate** (`spring.jpa.hibernate.ddl-auto=update`) crea las tablas del negocio a partir de las entidades JPA de `Infrastructure/Persistence/Entities`: `Convocation`, `ConvocationRequirement`, `Request` y `RequestRequirement`.

```mermaid
erDiagram
    ConvocationType ||..o{ Convocation : "clasifica"
    Convocation ||..o{ ConvocationRequirement : "exige"
    Requirement ||..o{ ConvocationRequirement : "es del tipo"
    Convocation ||..o{ Request : "recibe"
    Student ||..o{ Request : "envía"
    Request ||..|{ RequestRequirement : "responde"
    ConvocationRequirement ||..o{ RequestRequirement : "se responde con"
    Student |o--o| Users : "inicia sesión como"

    ConvocationType {
        int convocationTypeId PK "1 MONITORING, 2 GRANT, 3 RESIDENCE"
        nvarchar convocationTypeName
    }
    Convocation {
        int id PK
        varchar title
        varchar convocationDescription
        int convocationTypeId "ConvocationType"
        date startDate
        date endDate
        int stateConvocationId "EnumConvocationState: 1 OPEN, 2 CLOSED, 3 FINISHED, 4 CANCELLED"
        varchar cancellationReason "null salvo si está cancelada"
    }
    Requirement {
        int requirementId PK
        nvarchar requirementName "Promedio, Estrato, Semestres..."
        bit isGrade "1 si el valor es una nota de 0 a 5"
    }
    ConvocationRequirement {
        int id PK
        varchar name
        varchar requiredValue
        int conditionalId "EnumConditionRequirement: EQUAL, GREATER_OR_EQUAL..."
        varchar requirementDescription
        int convocationId "Convocation"
        int requirementId "Requirement"
    }
    Student {
        int id PK
        nvarchar nameStudent
        nvarchar email
        nvarchar program
        decimal average "promedio de 0 a 5.0"
        int stratum
    }
    Request {
        int id PK
        date creationDate
        int requestState "EnumStateRequest: 1 PENDING, 2 ACTIVE, 3 REJECT, 4 ACCEPT, 5 CANCELLED"
        int convocationId "Convocation"
        int studentId "Student"
        varchar comment "justificación del rechazo"
    }
    RequestRequirement {
        int id PK
        varchar requestValue "el valor que dio el estudiante"
        varchar attachment "adjunto opcional"
        int requestId "Request"
        int convocationRequirementId "ConvocationRequirement"
    }
    Users {
        int id PK
        nvarchar email UK
        nvarchar passwordHash "BCrypt, nunca la contraseña"
        nvarchar role "ADMIN o STUDENT"
        int studentId FK "null para el administrador"
    }
```

## Cómo leerlo

- **Línea continua: llave foránea real.** La única es `Users.studentId → Student.id`, que crea `create-database.sql` junto con una regla (`CHECK`): un usuario `STUDENT` siempre tiene `studentId` y el `ADMIN` nunca.
- **Línea punteada: relación que mantiene la aplicación.** Las tablas que crea Hibernate guardan la otra tabla por su id (`convocationId`, `studentId`...) pero sin `FOREIGN KEY`, porque las entidades JPA no usan `@ManyToOne`. Es una decisión del proyecto: el dominio trabaja con ids y cada servicio decide qué cargar. La consecuencia es que **la integridad la cuida el código**: por ejemplo, `RequestService` revisa que existan el estudiante y la convocatoria antes de guardar una solicitud, y `ConvocationService` no deja eliminar una convocatoria que tiene solicitudes.
- **Los estados y las condiciones no tienen tabla.** `stateConvocationId`, `requestState` y `conditionalId` son el número de un `enum` del dominio (`EnumConvocationState`, `EnumStateRequest`, `EnumConditionRequirement`); los mappers de persistencia los convierten. `convocationTypeId` sí tiene tabla, `ConvocationType`, y sus ids coinciden con `EnumConvocationType`.
- **Una solicitud por estudiante y convocatoria** es una regla del servicio (`existsByStudentIdAndConvocationId` → 409), no un índice único de la base.

## Cardinalidades

| Relación | Se lee |
|---|---|
| `ConvocationType` – `Convocation` | Un tipo clasifica cero o muchas convocatorias. |
| `Convocation` – `ConvocationRequirement` | Una convocatoria exige cero o muchos requisitos; cada requisito es de una sola convocatoria. |
| `Requirement` – `ConvocationRequirement` | Un tipo del catálogo (por ejemplo *Promedio*) se usa en muchos requisitos concretos (*Promedio mayor o igual a 4.0*). |
| `Convocation` – `Request` y `Student` – `Request` | Un estudiante envía muchas solicitudes, pero a lo sumo una por convocatoria. |
| `Request` – `RequestRequirement` | Una solicitud tiene una respuesta por cada requisito de su convocatoria (al menos una si la convocatoria tiene requisitos). |
| `Student` – `Users` | Cada estudiante con acceso tiene un usuario; el administrador es un usuario sin estudiante. |

Cómo se ve esto en el código está en el [modelo de dominio](diagrama-clases.md#2-modelo-de-dominio): las entidades del dominio tienen los mismos ids, y `ConvocationPersistenceMapper` y los demás mappers convierten entre la tabla y la entidad.
