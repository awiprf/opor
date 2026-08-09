# Java Spring Boot (Gradle) Microservices Root

This directory contains the highly granular, single-responsibility Spring Boot (Kotlin DSL + Gradle) microservices ecosystem for the project.

---

## Directory Architecture & Overview

```text
grd/
├── buildSrc/                                 # Centralized Gradle Convention Plugins (BUILD-TIME ONLY)
│   └── build.gradle.kts
│   └── src/main/kotlin/
│       └── java-service-convention.gradle.kts
├── lib/                                      # Shared Runtime Libraries (consumed by services)
│   └── cfg/                                  # Shared Configurations
│       └── dbu/                              # db-user connection config
├── settings.gradle.kts                       # Multi-project registry (includes all subprojects)
├── build.gradle.kts                          # Root build orchestration
├── README.md                                 # Architecture & Naming Guidelines (This File)
├── Backend-Gradle-Kotlin-Template.md         # Per-Microservice Internal Structure Reference
└── svc/                                      # Services directory (Domain Driven)
    ├── aut/                                  # Auth Domain
    ├── bkg/                                  # Booking Domain
    ├── odr/                                  # Order Domain
    ├── prd/                                  # Product Domain
    └── pym/                                  # Payment Domain
```

---

## Technical & Naming Conventions

To maintain cross-platform compatibility (Windows/Linux/Docker CI), clean Gradle subproject path resolution, and readability, all directories adhere strictly to the following standards:

1. **Strict 3-Letter Abbreviations**: All domain and service directories use **3-letter lowercase identifiers** (e.g., `aut` for Auth, `sgu` for Signup, `vec` for Verification Email Checker).

2. **Strict Sanitization (No Spaces, Dots, or Parentheses)**: Physical directory names **MUST NOT** contain spaces, leading/trailing numbers with dots (`1.`), or special characters. Descriptive documentation belongs exclusively inside `README.md` files.

3. **Gradle Subproject Resolution**: Every individual endpoint or worker/consumer inside `svc/` is registered as a subproject in `settings.gradle.kts` using standard colon syntax:

   ```kotlin
   include(
       "svc:aut:sgu:usn:vec",
       "svc:aut:sgu:usn:vps"
   )
   ```

4. **Build Logic Standardization**: All microservices apply `id("java-service-convention")` from `buildSrc` to share dependency management, Java toolchains (Java 17), and test setups without duplicate build code.

---

## Domain Breakdown

| Code  | Domain Name      | Description                                                          |
| :---- | :--------------- | :------------------------------------------------------------------- |
| `aut` | Auth Domain      | Authentication, user sign-up, sign-in, session management, password reset, token management, account removal. |
| `bkg` | Booking Domain   | Reservation schedules, time slots, and booking flow.                 |
| `odr` | Order Domain     | Order creation, lifecycle state machine, and fulfillment.            |
| `prd` | Product Domain   | Catalog management, inventory items, and listings.                   |
| `pym` | Payment Domain   | Payment processing, gateways, and transaction logs.                  |

---

## Auth Domain (`aut`) Sub-Domain Breakdown

| Code  | Sub-Domain Name   | Description                                                  |
| :---- | :---------------- | :----------------------------------------------------------- |
| `sgu` | Auth Signup       | User registration, verification, and onboarding flows.       |
| `sgi` | Auth Signin       | User login, session creation, and credential validation.     |
| `sgo` | Auth Signout      | Session termination and logout handling.                     |
| `rmv` | Auth Removal      | Account deletion, data cleanup, and off-boarding.            |

---

## Developer Onboarding & Local Build Commands

### Prerequisites

- **JDK 17+** installed.
- Gradle wrapper configured at project root.

### Common Commands

**Build All Services:**

```bash
./gradlew build
```

**Build Specific Endpoint/Worker:**

```bash
./gradlew :svc:aut:sgu:usn:vec:build
```

**Run Single Microservice Locally:**

```bash
./gradlew :svc:aut:sgu:usn:vec:bootRun
```
