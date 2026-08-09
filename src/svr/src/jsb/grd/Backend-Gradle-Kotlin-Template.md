# Spring Boot (Gradle Kotlin DSL) — Directory Structure Mapping

## The Node.js Express Flow (for reference)

```text
routes → controllers → services → repositories / utils
```

Each leaf microservice (e.g. `google-signup`) is a standalone Express app with this internal structure under `src/`.

---

## The Spring Boot Equivalent

In Spring Boot, the **same conceptual layers** exist, but they live inside the standard Maven/Gradle source layout (`src/main/java/...` + `src/main/resources/`). Here's the **high-level directory structure** inside each leaf microservice.

### Per-Microservice Internal Layout (e.g. `vec/`)

```text
vec/                                          # Leaf microservice (Gradle subproject)
├── build.gradle.kts                          # Applies java-service-convention plugin
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/opor/aut/sgu/usn/vec/    # Base package (reverse-domain + path)
    │   │       │
    │   │       ├── controller/               # ⟵ Express "routes" + "controllers"
    │   │       │                             #   @RestController classes
    │   │       │                             #   Route mapping (@GetMapping etc.) lives HERE
    │   │       │                             #   Orchestrates which service to call
    │   │       │
    │   │       ├── service/                  # ⟵ Express "services"
    │   │       │                             #   @Service classes
    │   │       │                             #   Business logic, orchestration, validation
    │   │       │
    │   │       ├── repository/               # ⟵ Express "repositories"
    │   │       │                             #   @Repository interfaces (Spring Data JPA / etc.)
    │   │       │                             #   One per table/entity
    │   │       │
    │   │       ├── model/                    # Entity / domain model classes
    │   │       │                             #   @Entity, @Table — . DB table mappings
    │   │       │                             #   (No direct Node.js equivalent — we'd inline this)
    │   │       │
    │   │       ├── dto/                      # Data Transfer Objects (request/response shapes)
    │   │       │                             #   Equivalent to . req.body / res.json shapes
    │   │       │
    │   │       ├── config/                   # Spring @Configuration classes
    │   │       │                             #   Bean definitions, CORS, security, etc.
    │   │       │
    │   │       ├── util/                     # ⟵ Express "utils"
    │   │       │                             #   Static helper methods, formatters, validators
    │   │       │
    │   │       ├── exception/                # Custom exceptions + @ControllerAdvice handler
    │   │       │                             #   Centralised error handling (like Express error middleware)
    │   │       │
    │   │       └── VecApplication.java       # @SpringBootApplication entry point
    │   │                                     #   Equivalent to . index.js that starts the Express server
    │   │
    │   └── resources/
    │       ├── application.yml               # Spring config (port, DB url, env vars)
    │       │                                 #   Equivalent to . .env / config files
    │       └── application-{profile}.yml     # Profile-specific overrides (dev, staging, prod)
    │
    └── test/
        └── java/
            └── com/opor/aut/sgu/usn/vec/    # Mirror of main — test classes go here
                ├── controller/
                ├── service/
                └── repository/
```

---

## Layer-by-Layer Mapping

| Node.js Express Layer | Spring Boot Equivalent | Key Annotations / Notes |
|:---|:---|:---|
| `routes/index.js` | `controller/` | `@RestController`, `@RequestMapping`, `@GetMapping` etc. — route definitions **and** request handling live in the same class |
| `controllers/index.js` | `controller/` | In Spring Boot, routes + controller logic are **merged** into one `@RestController` class (no separate route file) |
| `services/*.js` | `service/` | `@Service` — same concept: business logic, called by controllers |
| `repositories/index.js` | `repository/` | `@Repository` — Spring Data interfaces, one per entity/table |
| `utils/*.js` | `util/` | Plain Java classes with static helpers |
| `index.js` (Express app start) | `VecApplication.java` | `@SpringBootApplication` — the `main()` entry point |
| `.env` / config | `resources/application.yml` | Externalised config via Spring profiles |
| Express error middleware | `exception/` | `@ControllerAdvice` + `@ExceptionHandler` |
| *(implicit in JS)* | `model/` | `@Entity` JPA classes — explicit in Java |
| *(implicit in JS)* | `dto/` | Request/Response POJOs — explicit in Java |

---

## Key Differences from Node.js to Understand

> [!IMPORTANT]
> **Routes + Controllers are merged in Spring Boot.** In Express we separate `routes/` (URL mapping) from `controllers/` (handler logic). In Spring Boot, `@RestController` combines both — the annotations on methods define the route AND the handler.

> [!NOTE]
> **`model/` and `dto/` are new layers.** Java is strongly typed, so we explicitly define entity classes (`model/`) for . DB tables and DTO classes (`dto/`) for request/response payloads. In Node.js these are implicit JS objects.

> [!NOTE]
> **`config/` replaces scattered Express middleware setup.** Things like CORS, security filters, bean wiring — all go into `@Configuration` classes.

---

## Full Monorepo Tree (High-Level Overview)

Zooming out to show how this fits into . existing `grd/` structure:

```text
grd/
├── buildSrc/                                # Shared Gradle convention plugins
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── java-service-convention.gradle.kts
│
├── settings.gradle.kts                      # Registers all subprojects
├── build.gradle.kts                         # Root build orchestration
├── README.md
│
└── svc/                                     # All domain services
    └── aut/                                 # Auth domain
        └── sgu/                             # Signup sub-domain
            └── usn/                         # Username provider
                └── vec/                     # ← ONE hyper-granular microservice
                    ├── build.gradle.kts
                    └── src/
                        ├── main/
                        │   ├── java/com/opor/aut/sgu/usn/vec/
                        │   │   ├── controller/
                        │   │   ├── service/
                        │   │   ├── repository/
                        │   │   ├── model/
                        │   │   ├── dto/
                        │   │   ├── config/
                        │   │   ├── util/
                        │   │   ├── exception/
                        │   │   └── VecApplication.java
                        │   └── resources/
                        │       └── application.yml
                        └── test/java/...
```

---

## Summary: What Goes Where

| We want to... | Put it in... |
|:---|:---|
| Define an API endpoint | `controller/` |
| Write business logic | `service/` |
| Talk to a database | `repository/` + `model/` |
| Shape request/response payloads | `dto/` |
| Configure beans, CORS, security | `config/` |
| Helper/utility functions | `util/` |
| Centralised error handling | `exception/` |
| App entry point | `*Application.java` at package root |
| Env vars, DB URLs, port | `resources/application.yml` |
