## clean-architecture-api

Project for see clean architecture examples

## Requirements

- Java 17
- Docker and Docker Compose
- RabbitMQ running from the `clean-architecture-notification` repo (`node/docker-compose.yml`, port `5673`)

## Run with Docker

1. Start RabbitMQ from the notification repo:

   ```bash
   cd ../clean-architecture-notification/node
   docker compose up -d
   ```

2. Start SQL Server and the backend from this repo:

   ```bash
   docker compose up -d --build
   ```

   The `sqlserver` container runs `create-database.sql` on startup (creates `CleanArchitectureDB` and seeds the
   `Requirement` and `ConvocationType` tables). The `backend` waits until the database is healthy before starting.

| Service    | URL / Port                                   |
|------------|----------------------------------------------|
| Backend    | http://localhost:3001/ms-logica-negocio/api  |
| SQL Server | `localhost:1435` (user `sa`, password in `sqlserver.Dockerfile`) |

Quick check: `GET http://localhost:3001/ms-logica-negocio/api/test/hello`

## Configuration

`src/main/resources/application-dev.properties` reads these environment variables (defaults in parentheses):

| Variable        | Default     |
|-----------------|-------------|
| `DB_HOST`       | `localhost` |
| `DB_PORT`       | `1433`      |
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_PORT` | `5673`      |

Inside Docker Compose the backend uses `sqlserver:1433` and reaches RabbitMQ through `host.docker.internal:5673`.

## Scripts

### `gradlew build`

Install dependencies and execute the proyect
