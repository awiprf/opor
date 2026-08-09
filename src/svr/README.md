# Server-Side Codebase (`svr`)

This directory is the root of the entire server-side infrastructure for the **opor** project. It houses a hyper-granular, single-endpoint-per-microservice architecture where every exposed service is implemented across **four independent codebases** spanning two language domains and four framework variants.

---

## Core Philosophy

### 1 Endpoint = 1 Microservice = 4 Codebases

Every microservice in this system maps to exactly **one endpoint** (or one worker/consumer). Each of those microservices is implemented **four times** — once per framework variant — ensuring that the system can be deployed on any supported runtime without architectural drift.

```text
┌──────────────────────────────────────────────────────────────────────┐
│                     Single Microservice (e.g., /sgi)                │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────── Java Spring Boot ───────────────────┐         │
│   │                                                         │         │
│   │   ┌─────────────┐          ┌─────────────┐             │         │
│   │   │  grd         │          │  mvn         │             │         │
│   │   │  Gradle +    │          │  Maven +     │             │         │
│   │   │  Kotlin DSL  │          │  .java       │             │         │
│   │   └─────────────┘          └─────────────┘             │         │
│   │                                                         │         │
│   └─────────────────────────────────────────────────────────┘         │
│                                                                      │
│   ┌─────────────────── Node.js ────────────────────────────┐         │
│   │                                                         │         │
│   │   ┌─────────────┐          ┌─────────────┐             │         │
│   │   │  exp         │          │  nsj         │             │         │
│   │   │  Express     │          │  NestJS      │             │         │
│   │   │  (Vanilla)   │          │  (Framework) │             │         │
│   │   └─────────────┘          └─────────────┘             │         │
│   │                                                         │         │
│   └─────────────────────────────────────────────────────────┘         │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

This approach guarantees:

- **Runtime Portability** — Deploy any endpoint on JVM or Node.js without rewriting business logic contracts.
- **Framework Interchangeability** — Swap between Gradle/Maven or Express/NestJS per-environment without cross-cutting dependency.
- **Parallel Development** — Teams can work on their preferred stack while respecting the same API contract per endpoint.
- **Benchmark & Comparison** — Directly benchmark identical services across runtimes and frameworks under identical traffic shapes.

---

## Directory Architecture & Overview

```text
svr/
├── README.md                           # Server-Side Root Architecture (This File)
└── src/
    ├── jsb/                            # Java Spring Boot Domain
    │   ├── grd/                        # Gradle + Kotlin DSL Codebase
    │   │   ├── buildSrc/               # Centralized Gradle Convention Plugins
    │   │   ├── settings.gradle.kts     # Multi-project registry
    │   │   ├── build.gradle.kts        # Root build orchestration
    │   │   ├── README.md               # Gradle-specific architecture docs
    │   │   └── svc/                    # Services directory (Domain Driven)
    │   │       ├── aut/                # Auth Domain
    │   │       ├── bkg/                # Booking Domain
    │   │       ├── odr/                # Order Domain
    │   │       ├── prd/                # Product Domain
    │   │       └── pym/                # Payment Domain
    │   │
    │   └── mvn/                        # Maven + .java Codebase
    │       ├── pom.xml                 # Root Maven POM (multi-module)
    │       └── svc/                    # Services directory (mirrors grd/svc/)
    │           ├── aut/
    │           ├── bkg/
    │           ├── odr/
    │           ├── prd/
    │           └── pym/
    │
    └── nde/                            # Node.js Domain
        ├── exp/                        # Express Vanilla Codebase
        │   └── svc/                    # Services directory (mirrors grd/svc/)
        │       ├── aut/
        │       ├── bkg/
        │       ├── odr/
        │       ├── prd/
        │       └── pym/
        │
        └── nsj/                        # NestJS Codebase
            └── svc/                    # Services directory (mirrors grd/svc/)
                ├── aut/
                ├── bkg/
                ├── odr/
                ├── prd/
                └── pym/
```

---

## Language Domain Breakdown

| Code  | Domain        | Description                                                                               |
| :---- | :------------ | :---------------------------------------------------------------------------------------- |
| `jsb` | Java Spring Boot | JVM-based microservice implementations using Spring Boot as the foundational framework.  |
| `nde` | Node.js       | JavaScript/TypeScript-based microservice implementations running on the Node.js runtime.  |

---

## Framework Variant Breakdown

| Code  | Parent | Framework          | Build Tool / Style     | Language    | Description                                                                                          |
| :---- | :----- | :----------------- | :--------------------- | :---------- | :--------------------------------------------------------------------------------------------------- |
| `grd` | `jsb`  | Spring Boot        | Gradle + Kotlin DSL    | Kotlin/Java | Convention-plugin-driven Gradle multi-project build. All services apply shared `buildSrc` plugins.    |
| `mvn` | `jsb`  | Spring Boot        | Maven + POM            | Java        | Traditional Maven multi-module build with parent POM inheritance and `.java` source files.            |
| `exp` | `nde`  | Express (Vanilla)  | npm / package.json     | JavaScript  | Minimal, zero-abstraction Express.js services. Direct route handlers with no decorator overhead.      |
| `nsj` | `nde`  | NestJS             | npm / nest-cli.json    | TypeScript  | Full NestJS framework with modules, controllers, services, DTOs, and decorator-driven DI.             |

---

## Business Domain Breakdown

All four framework variants share an identical domain structure under `svc/`. The domain hierarchy is consistent across `grd/svc/`, `mvn/svc/`, `exp/svc/`, and `nsj/svc/`.

| Code  | Domain Name    | Description                                                                |
| :---- | :------------- | :------------------------------------------------------------------------- |
| `aut` | Auth           | Authentication, user sign-up, sign-in, session management, password reset, token management, account removal.|
| `bkg` | Booking        | Reservation schedules, time slots, and booking flow.                       |
| `odr` | Order          | Order creation, lifecycle state machine, and fulfillment.                  |
| `prd` | Product        | Catalog management, inventory items, and listings.                         |
| `pym` | Payment        | Payment processing, gateways, and transaction logs.                        |

---

## Auth Domain (`aut`) Sub-Domain Breakdown

| Code  | Sub-Domain Name   | Exposed Endpoint / Role | Description                                                  |
| :---- | :---------------- | :---------------------- | :----------------------------------------------------------- |
| `sgu` | Auth Signup       | `/sgu`                  | User registration, verification, and onboarding flows.       |
| `sgi` | Auth Signin       | `/sgi`                  | User login, session creation, and credential validation.     |
| `sgo` | Auth Signout      | `/sgo`                  | Session termination and logout handling.                     |
| `rmv` | Auth Removal      | `/rmv`                  | Account deletion, data cleanup, and off-boarding.            |

---

## Cross-Codebase Endpoint Mapping Example

To illustrate the four-codebase principle, here is how a single sub-domain endpoint (`sgu/usn/vec` — Verification Email Checker) maps across all variants:

| # | Framework Variant | Physical Path                                         | Build Artifact / Entrypoint   |
| :-- | :-------------- | :---------------------------------------------------- | :---------------------------- |
| 1 | Gradle (Kotlin)   | `svr/src/jsb/grd/svc/aut/sgu/usn/vec/`              | `build.gradle.kts`            |
| 2 | Maven (Java)      | `svr/src/jsb/mvn/svc/aut/sgu/usn/vec/`              | `pom.xml`                     |
| 3 | Express (JS)      | `svr/src/nde/exp/svc/aut/sgu/usn/vec/`              | `package.json`                |
| 4 | NestJS (TS)       | `svr/src/nde/nsj/svc/aut/sgu/usn/vec/`              | `package.json` / `nest-cli.json` |

Each of the four directories contains a self-contained, independently deployable microservice that exposes the **exact same API contract**.

---

## Naming & Directory Conventions

To maintain cross-platform compatibility (Windows/Linux/Docker CI), clean build-tool path resolution, and universal readability:

1. **Strict 3-Letter Abbreviations** — All domain, sub-domain, provider, and service directories use **3-letter lowercase identifiers** (e.g., `aut` for Auth, `sgu` for Signup, `vec` for Verification Email Checker).

2. **No Spaces, Dots, or Special Characters** — Physical directory names **MUST NOT** contain spaces, leading/trailing numbers with dots (`1.`), parentheses, or any special characters. Descriptive documentation belongs exclusively inside `README.md` files at each level.

3. **Mirror Structure Across Variants** — The `svc/` subtree **MUST** be structurally identical across `grd/`, `mvn/`, `exp/`, and `nsj/`. If a service exists in one variant, its corresponding directory must exist in all four, even if the implementation is pending.

4. **README.md at Every Significant Level** — Each domain, sub-domain, and provider directory should contain a `README.md` explaining the purpose, child services, execution flow, and mapping table.

---

## Service Type Classification

Each leaf-level microservice under a sub-domain is classified as one of the following:

| Type           | Abbreviation | Description                                                                                               |
| :------------- | :----------- | :-------------------------------------------------------------------------------------------------------- |
| HTTP Endpoint  | EP           | A REST endpoint that receives client requests and returns synchronous responses.                          |
| Publisher      | PUB          | A job fanner that publishes event payloads to a message queue/broker for asynchronous downstream processing.|
| Consumer       | CON          | A worker/consumer that subscribes to a message queue/broker and processes events asynchronously.           |

---

## Relationship to Client-Side (`cln`)

The server-side (`svr`) and client-side (`cln`) codebases live as sibling directories under `src/`. They share no source code but are coupled through the API contract defined by each microservice endpoint.

```text
opor/
└── src/
    ├── cln/    # Client-Side Codebase (Frontend / Mobile)
    └── svr/    # Server-Side Codebase (This Directory)
```

---

## Developer Onboarding

### Choosing a Framework Variant

| If You Are...                                   | Start With   | Path                    |
| :---------------------------------------------- | :----------- | :---------------------- |
| A JVM developer comfortable with Gradle/Kotlin  | `grd`        | `svr/src/jsb/grd/`     |
| A JVM developer preferring Maven/Java           | `mvn`        | `svr/src/jsb/mvn/`     |
| A Node.js developer wanting minimal abstraction | `exp`        | `svr/src/nde/exp/`     |
| A Node.js developer preferring full framework   | `nsj`        | `svr/src/nde/nsj/`     |

### Where to Find Detailed Documentation

Each framework variant root contains its own `README.md` with build commands, convention plugin details, and variant-specific onboarding:

| Variant | README Location                    |
| :------ | :--------------------------------- |
| Gradle  | `svr/src/jsb/grd/README.md`       |
| Maven   | `svr/src/jsb/mvn/README.md`       |
| Express | `svr/src/nde/exp/README.md`       |
| NestJS  | `svr/src/nde/nsj/README.md`       |

Sub-domain `README.md` files (e.g., `svc/aut/sgu/README.md`) contain the service breakdown tables, execution flow diagrams, and directory mapping for that specific sub-domain.
