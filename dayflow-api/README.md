# DayFlow API

DayFlow is a modular-monolith API for personal organization. The current foundation provides Oracle XE connectivity, Flyway, JPA auditing, OpenAPI, safe error defaults, basic security boundaries, profiles, and Docker development infrastructure.

## Requirements

- Java 21
- Docker Desktop (recommended for local Oracle XE)
- Maven Wrapper (included)

## Local setup

1. Copy `.env.example` to `.env` and set the local Oracle password.
2. Start Oracle XE: `docker compose up -d`.
3. Run the API: `./mvnw spring-boot:run` on Linux/macOS, or `./mvnw.cmd spring-boot:run` on Windows.

The local profile imports `.env` as a properties file. It is ignored by Git and must never be committed.

## Initial administrator

The first administrator is optional and is never created from a Flyway migration. Set `DAYFLOW_BOOTSTRAP_ADMIN_ENABLED=true` and all the `DAYFLOW_BOOTSTRAP_ADMIN_*` values in `.env` to create it on startup. The bootstrap is idempotent: an existing email is never changed. Disable the flag again after the first successful startup.

## Profiles

`local` is the default profile. `dev` and `prod` require `DAYFLOW_DB_URL`, `DAYFLOW_DB_USERNAME`, and `DAYFLOW_DB_PASSWORD` to be supplied by their environment. `test` runs against in-memory H2 and is activated by the test suite.

## Database and migrations

The Docker Oracle XE instance listens at `localhost:1522`; the default PDB is `FREEPDB1`. Hibernate validates the schema only (`ddl-auto=validate`). Every schema change belongs in a versioned SQL file under `src/main/resources/db/migration` and is applied by Flyway.

## API documentation and operations

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Health: `http://localhost:8080/actuator/health`

Only health and info Actuator endpoints are exposed. The remaining API surface is secured by default; JWT authorization will replace the temporary HTTP Basic boundary when the authentication module is implemented.

## Users API

The user module exposes `POST`, `GET`, `PUT`, and logical `DELETE` operations at `/api/v1/users`. It also supports `PATCH /api/v1/users/{id}/status` and `PATCH /api/v1/users/{id}/password`. User responses never contain password hashes. The endpoints are temporarily public while JWT authentication is pending; restrict them when the authentication module is enabled.

## Tasks API

Tasks belong to an active user and expose `POST`, `GET`, `PUT`, and logical `DELETE` operations at `/api/v1/tasks`. Listing requires `userId` and supports `active`, `status`, `priority`, and `title` filters with pagination. Completed or inactive tasks cannot be modified. Valid status transitions are enforced by the domain model.

## Tests

Run `./mvnw test` on Linux/macOS or `./mvnw.cmd test` on Windows.

## Architecture

The root package is `cl.dayflow.api`. Business capabilities will be organized by domain (`user`, `task`, and future modules), with `domain`, `application`, and `infrastructure` subpackages only where each separation has a concrete responsibility. Cross-cutting technical concerns live in `shared`.
