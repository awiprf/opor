The **Verification Email Checker (`/vec`)** microservice is fully operational, verified, and running end-to-end on Java 21, Kotlin, and Spring Boot 3.3.2.

Here is everything the next developer needs to know to take over and build upon this codebase.

---

**Current Status of `/vec**`

* **Functionality**: `GET /api/v1/aut/check-email?email=user@example.com` validates syntax and performs a cache-aside availability check (Redis first $\rightarrow$ PostgreSQL fallback with 60-second TTL).
* **Execution & Environment**: Domain-level variables live in `svc/aut/.env.local`. The service is launched locally from `src/svr/src/jsb/grd/` using the executable script:
```powershell
.\run-vec.ps1

```


* **Postman Verification**: Tested and working with live database queries returning `200 OK` (`{"email": "...", "available": true/false}`).

---

**Architecture & Code Placement Rules**

The codebase follows a 3-layer Gradle architecture (`buildSrc` $\rightarrow$ `lib/` $\rightarrow$ `svc/`).

| Layer / Package | Location | Purpose & Developer Rule |
| --- | --- | --- |
| **Shared DB Config** | `lib/cfg/dbu/db-aut` | Contains shared `@Configuration` beans for PostgreSQL domain connections ("How to connect"). |
| **Shared Redis Config** | `lib/cfg/rds/rd-aut` | Contains shared `@Configuration` beans for RedisTemplate connections. |
| **Local Model** | `vec/model/EmailRecord.kt` | **Service-Resident**. Maps only `id` and `email` columns to the `users` table. *Never put entities in shared libs to prevent "God entity" bloat.* |
| **Local Repository** | `vec/repository/` | **Service-Resident**. `EmailRecordRepository.kt` handles slim `existsByEmail()` queries. |
| **Local Business Logic** | `vec/service/`, `vec/controller/`, `vec/dto/` | **Service-Resident**. Contains `EmailCheckService`, `EmailCheckController`, `EmailCheckResponse`, and `EmailValidator`. |
| **Scaffold Conventions** | `vec/mapper/`, `vec/security/` | Empty directories containing `.gitkeep` retained as monorepo structure documentation. |

---

**Next Developer Roadmap & Action Items**

1. **Analytics Integration (OLAP / ClickHouse)**
* **OLTP vs. OLAP**: PostgreSQL handles relational OLTP checks (`existsByEmail`).
* **Third-Party / ClickHouse**: Integrate the third-party analytics API or ClickHouse driver to log high-volume inspection events for massive data analysis. Create a dedicated client configuration inside `vec/config/` (or a shared `lib/cfg/` module if other microservices will emit analytics events).


2. **Rate Limiting & Security Refinement**
* **Bucket4j Cleaning**: Update `RateLimitConfig.kt` to clean up Bucket4j v8+ deprecation warnings regarding `Refill` and `Bandwidth.classic` methods.
* **Security Architecture**: Reusable JWT/Auth infrastructure belongs in a new `lib:cfg:sec` module. Service-specific endpoint authorization rules must be written inside `vec/config/` or `vec/security/`.


3. **Pipeline Microservice Expansion**
* Implement remaining microservices under `svc/aut/sgu/usn/` (`vps` OTP publisher, `vck` KV consumer, `vce` emailer, `vsc` code submit endpoint). Each service must reuse `lib:cfg:dbu:db-aut` and `lib:cfg:rds:rd-aut` while maintaining its own local models.