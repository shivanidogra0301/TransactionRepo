# Transactions Service

A small Spring Boot service that exposes three endpoints to manage cardholder accounts and their transactions.

## Stack

- Java 17, Spring Boot 3.2
- Jersey (JAX-RS), Spring Data JPA, Bean Validation
- H2 in-memory database
- Gradle (Groovy DSL), JUnit 5
- Docker

## Run

```bash
./run         # docker compose up if Docker is available, otherwise ./gradlew bootRun
./run test    # run tests
./run build   # build a runnable jar (build/libs/transactions-service-1.0.0.jar)
```

The app listens on `http://localhost:8080`. H2 console is at `/h2-console` (jdbc url `jdbc:h2:mem:transactions`, user `sa`, no password).

### With Gradle directly

```bash
./gradlew bootRun
./gradlew test
./gradlew bootJar
```

### With Docker directly

```bash
docker compose up --build
```

## Test coverage

Coverage is measured with [JaCoCo](https://www.jacoco.org/jacoco/) (`models/**` and `config/**` are intentionally excluded — see `build.gradle`).
This project has 98% test coverage.

```bash
./gradlew test jacocoTestReport     # runs all tests and generates the report
./run test                          # equivalent shortcut
```

After the build completes, the reports are written to:

- HTML (interactive, recommended): `build/reports/jacoco/test/html/index.html`
- XML (for CI / coverage badges): `build/reports/jacoco/test/jacocoTestReport.xml`

Open the HTML report in a browser to drill down per package/class/line:

```bash
open build/reports/jacoco/test/html/index.html      # macOS
xdg-open build/reports/jacoco/test/html/index.html  # Linux
```

## Endpoints

### POST `/accounts`

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"document_number": "12345678900"}'
```

Response `201`:

```json
{ "account_id": 1, "document_number": "12345678900" }
```

### GET `/accounts/{accountId}`

```bash
curl http://localhost:8080/api/accounts/1
```

Response `200`:

```json
{ "account_id": 1, "document_number": "12345678900" }
```

### POST `/transactions`

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H 'Content-Type: application/json' \
  -d '{"account_id": 1, "operation_type_id": 4, "amount": 123.45}'
```

Response `201`:

```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45,
  "event_date": "2026-05-04T10:32:07.7199222Z"
}
```

## Project layout

```
src/main/java/com/example/transactions
├── Main.java                         # Spring Boot entry point
├── config/                           # JerseyConfig
├── controllers/                      # JAX-RS resources (Account, Transaction)
├── exceptions/                       # ExceptionMappers, custom exceptions
├── logger/                           # RequestLoggingFilter
├── models/
│   ├── entities/                     # Account, Transaction (JPA)
│   ├── enums/                        # OperationType
│   ├── requests/                     # Create*Request DTOs
│   └── response/                     # *Response DTOs
├── repository/                       # Spring Data JPA repositories
└── service/
    └── serviceImpl/                  # AccountService, TransactionService + impls
```
