# Record API

Spring Boot backend that accepts JSON record payloads over HTTP POST with token authorization, persistence, and request auditing.

## Prerequisites

- Java 17+
- Maven 3.9+

## Run the application

```bash
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. The bearer token is configured in `src/main/resources/application.properties` as `api.auth.token`.

## Sample payload

```json
{
  "RecordType": "xxx",
  "DeviceId": "357370040159770",
  "EventDateTime": "2014-05-12T05:09:48Z",
  "FieldA": 68,
  "FieldB": "xxx",
  "FieldC": 123.45
}
```

Mandatory fields: `RecordType`, `DeviceId`, `EventDateTime`.

## Authorization

Send the token in the `Authorization` header (use the value of `api.auth.token` from `application.properties`):

```
Authorization: Bearer <api.auth.token>
```

## curl demo

These examples use **Windows Command Prompt (cmd)** syntax. Replace `<api.auth.token>` with the value from `application.properties`.

### Echo — returns HTTP 200 and the original payload

```cmd
curl -i -X POST http://localhost:8080/echo ^
  -H "Authorization: Bearer <api.auth.token>" ^
  -H "Content-Type: application/json" ^
  -d "{\"RecordType\":\"xxx\",\"DeviceId\":\"357370040159770\",\"EventDateTime\":\"2014-05-12T05:09:48Z\",\"FieldA\":68,\"FieldB\":\"xxx\",\"FieldC\":123.45}"
```

### Device — returns HTTP 200 and only `DeviceId`

```cmd
curl -i -X POST http://localhost:8080/device ^
  -H "Authorization: Bearer <api.auth.token>" ^
  -H "Content-Type: application/json" ^
  -d "{\"RecordType\":\"xxx\",\"DeviceId\":\"357370040159770\",\"EventDateTime\":\"2014-05-12T05:09:48Z\",\"FieldA\":68,\"FieldB\":\"xxx\",\"FieldC\":123.45}"
```

### No content — returns HTTP 204 (any path ending in `/nocontent`)

```cmd
curl -i -X POST http://localhost:8080/api/v1/nocontent ^
  -H "Authorization: Bearer <api.auth.token>" ^
  -H "Content-Type: application/json" ^
  -d "{\"RecordType\":\"xxx\",\"DeviceId\":\"357370040159770\",\"EventDateTime\":\"2014-05-12T05:09:48Z\",\"FieldA\":68,\"FieldB\":\"xxx\",\"FieldC\":123.45}"
```

### Unknown path — returns HTTP 400

```cmd
curl -i -X POST http://localhost:8080/unknown ^
  -H "Authorization: Bearer <api.auth.token>" ^
  -H "Content-Type: application/json" ^
  -d "{\"RecordType\":\"xxx\",\"DeviceId\":\"357370040159770\",\"EventDateTime\":\"2014-05-12T05:09:48Z\",\"FieldA\":68,\"FieldB\":\"xxx\",\"FieldC\":123.45}"
```

### Missing token — returns HTTP 401

```cmd
curl -i -X POST http://localhost:8080/echo ^
  -H "Content-Type: application/json" ^
  -d "{\"RecordType\":\"xxx\",\"DeviceId\":\"357370040159770\",\"EventDateTime\":\"2014-05-12T05:09:48Z\"}"
```

## Endpoint behavior

| Path | HTTP status | Response body |
|------|-------------|---------------|
| `/echo` | 200 | Full original payload |
| `/device` | 200 | `{"DeviceId":"..."}` only |
| ends with `/nocontent` | 204 | Empty |
| anything else | 400 | Empty |

All valid requests are stored in the `records` table and logged in `audit_logs`.

## Integration tests

```bash
mvn test
```

## H2 console

While running locally, browse to `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:recorddb` and username `sa` (no password).
