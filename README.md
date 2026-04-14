# Gestión de Empleados API

REST API para gestión de empleados construida con Spring Boot 3 y Java 21. Incluye paginación, filtros dinámicos, manejo global de excepciones y cobertura de tests con JUnit 5 y Mockito.

## Stack

- **Java 21** · **Spring Boot 3.3.5** · **Maven**
- **Spring Data JPA** · **PostgreSQL** (producción) · **H2** (tests)
- **Bean Validation** — validación en DTOs con mensajes de error descriptivos
- **Springdoc OpenAPI** — documentación interactiva en `/swagger-ui.html`
- **JUnit 5 + Mockito** — unit tests e integration tests

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/empleados` | Listar empleados (paginado, filtros opcionales) |
| GET | `/api/empleados/{id}` | Obtener empleado por ID |
| POST | `/api/empleados` | Crear empleado |
| PUT | `/api/empleados/{id}` | Actualizar empleado |
| DELETE | `/api/empleados/{id}` | Eliminar empleado |

### Filtros disponibles en GET /api/empleados

```
GET /api/empleados?nombre=Juan&departamento=Backend&page=0&size=10&sort=apellido
```

## Arquitectura

```
Controller → Service (interface + impl) → Repository → Entity
```

- **Controller** — recibe requests, delega al service, retorna respuestas HTTP
- **Service** — lógica de negocio, mapeo Entity ↔ DTO
- **Repository** — acceso a datos con JPQL para filtros dinámicos
- **GlobalExceptionHandler** — manejo centralizado de errores (404, 400, 409, 500)

## Setup

### Requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### Base de datos

```sql
CREATE DATABASE gestion_empleados;
```

### Configuración

Editá `src/main/resources/application.properties` con tus credenciales de PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestion_empleados
spring.datasource.username=postgres
spring.datasource.password=tu_password
```

### Correr la aplicación

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Tests

Los tests corren con H2 en memoria — no necesitan PostgreSQL instalado.

```bash
mvn test
```

### Cobertura

| Clase | Tipo | Tests |
|-------|------|-------|
| `EmpleadoServiceTest` | Unit (Mockito) | 8 tests |
| `EmpleadoControllerTest` | Integration (MockMvc + H2) | 9 tests |

**`EmpleadoServiceTest`** — testea la lógica de negocio aislada del contexto Spring:
- `findAll` — retorna página correcta / página vacía
- `findById` — retorna empleado / lanza `ResourceNotFoundException`
- `create` — persiste y retorna el empleado
- `update` — actualiza correctamente / lanza excepción si no existe
- `delete` — elimina correctamente / lanza excepción si no existe

**`EmpleadoControllerTest`** — testea la API de punta a punta con base de datos real (H2):
- Paginación y filtros dinámicos
- Respuestas correctas (200, 201, 204)
- Manejo de errores (400 con `fieldErrors`, 404 con mensaje)

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/axeldev/gestionempleados/
│   │   ├── config/          # OpenAPI config
│   │   ├── controller/      # EmpleadoController
│   │   ├── dto/             # EmpleadoRequest, EmpleadoResponse
│   │   ├── exception/       # GlobalExceptionHandler, ResourceNotFoundException, ApiError
│   │   ├── model/           # Empleado (entity)
│   │   ├── repository/      # EmpleadoRepository
│   │   └── service/         # EmpleadoService + EmpleadoServiceImpl
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/axeldev/gestionempleados/
    │   ├── controller/      # EmpleadoControllerTest
    │   └── service/         # EmpleadoServiceTest
    └── resources/
        └── application.properties   # H2 config para tests
```
