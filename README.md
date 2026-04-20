# Gestión de Empleados API

![Java](https://img.shields.io/badge/Java-21+-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven&logoColor=white)
![Tests](https://img.shields.io/badge/Tests-17%20passing-brightgreen?logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

REST API para gestión de empleados con arquitectura en capas, filtros dinámicos, paginación y suite de tests con JUnit 5 + Mockito.

## Qué demuestra este proyecto

| Concepto | Implementación |
|----------|---------------|
| Arquitectura en capas | `Controller → Service (interface + impl) → Repository → Entity` |
| Testing con mocks | `EmpleadoServiceTest` — lógica de negocio aislada con Mockito |
| Integration testing | `EmpleadoControllerTest` — API end-to-end con MockMvc + H2 |
| Filtros dinámicos | JPQL con parámetros opcionales (`nombre`, `departamento`) |
| Paginación | Spring Data `Pageable` con sorting configurable |
| Manejo de errores | `GlobalExceptionHandler` — respuestas estructuradas para 400, 404, 500 |
| Validación | Bean Validation en DTOs con mensajes descriptivos |
| Documentación | Swagger/OpenAPI en `/swagger-ui.html` |

## Stack

- **Java 21** · **Spring Boot 3.4.4** · **Maven**
- **Spring Data JPA** · **PostgreSQL** (producción) · **H2** (tests)
- **Bean Validation** — validación en DTOs con mensajes de error descriptivos
- **Springdoc OpenAPI 2.6** — documentación interactiva en `/swagger-ui.html`
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

```http
GET /api/empleados?nombre=Juan&departamento=Backend&page=0&size=10&sort=apellido
```

Todos los parámetros son opcionales y combinables. Sin filtros devuelve todos los empleados paginados.

## Arquitectura

```
Controller → Service (interface + impl) → Repository → Entity
```

- **Controller** — recibe requests, delega al service, retorna respuestas HTTP correctas (200, 201, 204, 404, 400)
- **Service** — lógica de negocio, mapeo Entity ↔ DTO. La interface desacopla la implementación del controller
- **Repository** — acceso a datos con JPQL para filtros dinámicos opcionales
- **GlobalExceptionHandler** — manejo centralizado de errores con respuestas estructuradas tipo `ApiError`

### GlobalExceptionHandler — respuesta de error estructurada

```json
// POST /api/empleados con campo inválido → 400 Bad Request
{
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "fieldErrors": {
    "email": "debe ser una dirección de correo bien formada",
    "nombre": "no debe estar vacío"
  }
}
```

## Tests

Los tests corren con H2 en memoria — no necesitan PostgreSQL instalado.

```bash
mvn test
```

### Suite de tests

| Clase | Tipo | Tests | Qué verifica |
|-------|------|-------|--------------|
| `EmpleadoServiceTest` | Unit (Mockito) | 8 | Lógica de negocio aislada del contexto Spring |
| `EmpleadoControllerTest` | Integration (MockMvc + H2) | 9 | API end-to-end, respuestas HTTP, manejo de errores |

**`EmpleadoServiceTest`** — testea la lógica de negocio aislada:
- `findAll` — retorna página correcta / página vacía
- `findById` — retorna empleado / lanza `ResourceNotFoundException` (→ 404)
- `create` — persiste y retorna el empleado creado
- `update` — actualiza correctamente / lanza excepción si no existe
- `delete` — elimina correctamente / lanza excepción si no existe

**`EmpleadoControllerTest`** — testea la API de punta a punta con H2:
- Paginación y filtros dinámicos (nombre, departamento)
- Respuestas correctas (200, 201, 204)
- Manejo de errores (400 con `fieldErrors`, 404 con mensaje descriptivo)

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

Editá `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestion_empleados
spring.datasource.username=postgres
spring.datasource.password=tu_password
```

### Correr la aplicación

```bash
mvn spring-boot:run
```

Disponible en `http://localhost:8080` · Swagger UI: `http://localhost:8080/swagger-ui.html`

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
│   │   └── service/         # EmpleadoService (interface) + EmpleadoServiceImpl
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/axeldev/gestionempleados/
    │   ├── controller/      # EmpleadoControllerTest
    │   └── service/         # EmpleadoServiceTest
    └── resources/
        └── application.properties   # H2 config para tests
```

## Licencia

MIT
