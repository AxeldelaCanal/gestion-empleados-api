# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

- Java 25 · Spring Boot 3.4.4 · Maven
- Spring Data JPA · PostgreSQL (prod) · H2 (tests)
- Bean Validation · Springdoc OpenAPI 2.6.0
- JUnit 5 + Mockito (via spring-boot-starter-test)

## Commands

```bash
# Run all tests (H2 in-memory, no PostgreSQL needed)
mvn test

# Run a single test class
mvn test -Dtest=EmpleadoServiceTest
mvn test -Dtest=EmpleadoControllerTest

# Run the app (requires PostgreSQL running)
mvn spring-boot:run
```

**IntelliJ — required VM option for tests to pass on Java 25:**
`Run → Edit Configurations → Edit Configuration Templates → JUnit → VM options:`
```
-Dnet.bytebuddy.experimental=true
```
Without this, Mockito fails with "Java 25 is not supported by the current version of Byte Buddy".

## Architecture

Strict layered architecture — each layer only talks to the one below it:

```
EmpleadoController  →  EmpleadoService (interface)
                              ↓
                    EmpleadoServiceImpl  →  EmpleadoRepository
                                                   ↓
                                              Empleado (entity)
```

DTOs cross the boundary at the Controller and Service layers:
- `EmpleadoRequest` (record with Bean Validation) — inbound
- `EmpleadoResponse` (record) — outbound
- Mapping is done manually inside `EmpleadoServiceImpl#toResponse()`

`ApiError` (record) is the unified error envelope returned by `GlobalExceptionHandler` for all error cases (404, 400 with field errors, 409, 500).

## Key design decisions

**No Lombok** — removed due to incompatibility with Java 25 annotation processing. `Empleado` uses a hand-written builder pattern. All constructors and accessors are explicit.

**Dynamic filtering via JPQL** — `EmpleadoRepository` uses a `@Query` with nullable parameters (`IS NULL OR LIKE`) instead of Specifications to keep it simple.

**Test strategy:**
- `EmpleadoServiceTest` — pure unit tests with `@ExtendWith(MockitoExtension.class)`. No Spring context loaded. Mocks `EmpleadoRepository`, tests service logic in isolation.
- `EmpleadoControllerTest` — full integration tests with `@SpringBootTest + @AutoConfigureMockMvc`. Loads full Spring context with H2. Each test starts with `repository.deleteAll()` to ensure isolation.

**Test config** — `src/test/resources/application.properties` overrides the main config with H2 in-memory and `ddl-auto=create-drop`. No profile annotations needed.

## Java 25 compatibility notes

The `pom.xml` surefire plugin already includes the necessary flags:
```xml
-Dnet.bytebuddy.experimental=true
-XX:+EnableDynamicAgentLoading
--add-opens java.base/java.lang=ALL-UNNAMED
```
These are required because Mockito's ByteBuddy officially supports up to Java 24. Spring Boot 3.4.4+ (Hibernate 6.6.x) is required to avoid `TypeTag::UNKNOWN` errors during annotation processing.
