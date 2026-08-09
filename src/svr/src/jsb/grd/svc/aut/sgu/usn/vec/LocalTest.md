Here is your content transformed into clean, well-formatted Markdown:

---

## Step 3: Test with Postman

The microservice exposes a `GET` endpoint for email availability checks.

### Request Details

* **Method:** `GET`
* **URL:** `http://localhost:8081/api/v1/aut/check-email`
* **Query Parameters:** `email = testuser@example.com`

---

### Test Cases

| Scenario | Query Parameter | Expected HTTP Status | Expected JSON Body |
| --- | --- | --- | --- |
| **Available Email** | `email=newuser@example.com` | `200 OK` | `{"email": "newuser@example.com", "available": true}` |
| **Taken Email** | `email=existing@example.com` | `200 OK` | `{"email": "existing@example.com", "available": false}` |
| **Invalid Email Format** | `email=not-an-email` | `400 Bad Request` | *(Empty response body)* |
| **Rate Limit Exceeded** | Send > 10 requests within 1 minute | `429 Too Many Requests` | `{"error": "Too Many Requests", "message": "Rate limit exceeded..."}` |

---

### Postman Setup

1. Create a **New Request** in Postman.
2. Set the method to **`GET`**.
3. Enter the URL:
`http://localhost:8081/api/v1/aut/check-email?email=john@example.com`
4. Click **Send**.