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

## API Documentation (Swagger)

This project includes automatically generated OpenAPI 3.0 documentation and an interactive Swagger UI.

Once the application is running, you can explore the API contracts and test the endpoints directly from your browser:

- **Swagger UI (Interactive):** [http://localhost:8080/swagger.html](http://localhost:8080/swagger.html)
- **Raw OpenAPI JSON Blueprint:** [http://localhost:8080/api/openapi.json](http://localhost:8080/api/openapi.json)

*Tip: You can use the "Try it out" feature inside the Swagger UI to execute live requests against the in-memory database without needing a third-party client like Postman.*


## Project layout

```text
src/main/java/com/example/transactions
├── Main.java                         # Spring Boot entry point
├── config/                           # JerseyConfig
├── controllers/