# Auth Signup (`sgu`) Sub-Domain Architecture

This directory houses all single-responsibility microservices, publishers, and consumers handling user registration and verification flows for the **Auth Signup (`sgu`)** sub-domain.

---

## Provider Method Breakdown

| Identifier | Provider / Method | Description                                                              |
| :--------- | :---------------- | :----------------------------------------------------------------------- |
| `usn`      | Username / Email  | Standard email & username registration with 4-digit OTP verification.    |
| `apl`      | Apple Sign-In     | OAuth2 signup flow for Apple ID authentication.                          |
| `ggl`      | Google Sign-In    | OAuth2 signup flow for Google Identity services.                         |

---

## Sequence & Execution Flow Architecture (`usn` Provider)

The numbers assigned to the `usn` services reflect the **exact system execution order** as represented on system design diagrams and architectural sequence flows.

```text
[ Client Request ]
       │
       ▼
 1. vec (Email Checker Endpoint)  ─── Validates email availability & request format
       │
       ▼
 2. vps (OTP Job Publisher)       ─── Fans out event payload to message queue/broker
       │
       ├──▶ 3. vck (OTP Storage Consumer)  ─── Persists 4-digit code in KV cache (CloudFlare/Memcached)
       └──▶ 4. vce (OTP Email Consumer)    ─── Sends email containing 4-digit verification code
       │
 [ User Submits Code ]
       
       ▼
 5. vsc (Submit Code Endpoint)    ─── Validates 4-digit code against KV cache
       │
       ▼
 6. spm (Signup Payload Publisher) ── Publishes account creation event upon successful verification
       │
       ├──▶ 7. sce (Signup Email Consumer) ─── Sends welcome email confirmation
       ├──▶ 8. scp (Push Consumer)         ─── Triggers mobile push notification
       └──▶ 9. sci (In-App Consumer)       ─── Creates in-app onboarding notification record
```

---

## Directory Mapping & Actual Meanings (`usn`)

| Seq | Code  | Full Service Name                   | Service Type   | Role & Execution Responsibility                                                                                              |
| :-- | :---- | :---------------------------------- | :------------- | :---------------------------------------------------------------------------------------------------------------------------- |
| 1   | `vec` | Verification Email Checker          | HTTP Endpoint  | Initial REST endpoint to verify whether an email address is available and valid before sending verification tokens.            |
| 2   | `vps` | Verification OTP Job Publisher      | Publisher      | Job fanner/publisher that receives valid verification requests and publishes 4-digit code generation events to the message broker. |
| 3   | `vck` | Verification OTP KV Consumer        | Consumer       | Worker/consumer that consumes verification jobs and writes the 4-digit verification code to key-value storage (e.g., Redis).  |
| 4   | `vce` | Verification OTP Emailer Consumer   | Consumer       | Worker/consumer that consumes verification jobs and sends the 4-digit code email via SMTP/email service API.                  |
| 5   | `vsc` | Verification Submit Code Endpoint   | HTTP Endpoint  | REST endpoint where the client submits the received 4-digit verification code to validate against key-value storage.          |
| 6   | `spm` | Signup Payload Job Publisher         | Publisher      | Endpoint/publisher that receives final signup payload upon code verification and publishes the account creation event.        |
| 7   | `sce` | Signup Email Consumer               | Consumer       | Event worker/consumer that sends the welcome and confirmation email after successful account creation.                        |
| 8   | `scp` | Signup Push Notification Consumer   | Consumer       | Event worker/consumer that pushes onboarding notifications to mobile devices via APNs/FCM.                                   |
| 9   | `sci` | Signup In-App Notification Consumer | Consumer       | Event worker/consumer that inserts welcome messages and initial state into the in-app notification system database.           |

---

## Clean Physical Directory Tree

To maintain clean pathing across operating systems and build tools, physical folders are named cleanly without special characters or sequence prefixes:

```text
sgu/
├── README.md
├── apl/
├── ggl/
└── usn/
    ├── vec/    # (1) Verification Email Checker EP
    ├── vps/    # (2) Verification OTP Publisher
    ├── vck/    # (3) Verification OTP KV Consumer
    ├── vce/    # (4) Verification OTP Emailer Consumer
    ├── vsc/    # (5) Verification Submit Code EP
    ├── spm/    # (6) Signup Payload Publisher
    ├── sce/    # (7) Signup Email Consumer
    ├── scp/    # (8) Signup Push Consumer
    └── sci/    # (9) Signup In-App Consumer
```
