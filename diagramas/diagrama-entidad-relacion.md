# Diagrama entidad-relación

El diagrama entidad-relación muestra cómo están guardados los datos: qué tablas hay, qué columnas tiene cada una y cómo se relacionan. Las tablas de `CleanArchitectureDB` se crean en dos momentos:

- [`create-database.sql`](../create-database.sql) crea los catálogos y los datos de prueba: `Requirement`, `Student`, `ConvocationType` y `Users`.
- Hibernate crea el resto al arrancar el backend (`spring.jpa.hibernate.ddl-auto=update`), a partir de las entidades JPA de `Infrastructure/Persistence/Entities`: `Convocation`, `ConvocationRequirement`, `Request` y `RequestRequirement`.

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

Hay dos tipos de línea y la diferencia importa.

La **línea continua** es una llave foránea de verdad: SQL Server no deja guardar un valor que no exista en la otra tabla. Solo hay una, `Users.studentId → Student.id`. Además, `create-database.sql` le agrega una regla `CHECK`: un usuario `STUDENT` siempre tiene `studentId` y el `ADMIN` nunca.

La **línea punteada** es una relación que la base no conoce. Las tablas que crea Hibernate guardan el id de la otra tabla (`convocationId`, `studentId`...) sin `FOREIGN KEY`, porque las entidades JPA no usan `@ManyToOne`. El proyecto lo hace así a propósito, para que el dominio trabaje con ids y cada servicio decida qué cargar. El costo es que la integridad la tiene que cuidar el código. Por ejemplo, `RequestService` comprueba que existan el estudiante y la convocatoria antes de guardar una solicitud, y `ConvocationService` no deja borrar una convocatoria que ya tiene solicitudes.

Otros detalles que conviene notar:

- Los estados y las condiciones no tienen tabla. `stateConvocationId`, `requestState` y `conditionalId` guardan el número de un enum del dominio, y los mappers de persistencia hacen la conversión. `convocationTypeId` sí tiene tabla (`ConvocationType`), con los mismos ids que `EnumConvocationType`.
- La regla de una sola solicitud por estudiante en cada convocatoria tampoco está en la base. La aplica el servicio con `existsByStudentIdAndConvocationId`, que responde 409.

## Cardinalidades

| Relación | Cómo se lee |
|---|---|
| `ConvocationType` – `Convocation` | Un tipo puede tener muchas convocatorias. |
| `Convocation` – `ConvocationRequirement` | Una convocatoria tiene varios requisitos, y cada requisito pertenece a una sola. |
| `Requirement` – `ConvocationRequirement` | Un tipo del catálogo, como *Promedio*, se usa en muchos requisitos concretos, como *Promedio mayor o igual a 4.0*. |
| `Student` – `Request` – `Convocation` | Un estudiante puede tener muchas solicitudes, pero solo una por convocatoria. |
| `Request` – `RequestRequirement` | Una solicitud tiene una respuesta por cada requisito de su convocatoria. |
| `Student` – `Users` | Cada estudiante con acceso tiene un usuario. El administrador es un usuario sin estudiante. |

Para ver cómo se reflejan estas tablas en el código, compáralas con el [modelo de dominio](diagrama-clases.md#2-modelo-de-dominio): las entidades usan los mismos ids, y los mappers de persistencia pasan de una forma a la otra.
