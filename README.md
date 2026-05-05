# CO2 Emission API

This project is a small, production-ready REST service to estimate CO₂ emissions for freight shipments. It is implemented in Java 17 + Spring Boot and provides a single calculation endpoint, interactive OpenAPI/Swagger docs, API key authentication, and standardized JSON error responses (useful when publishing on marketplaces such as RapidAPI).

## What’s included
- Single POST endpoint to calculate CO₂ for a shipment (weight, distance, transport type)
- Clear OpenAPI documentation with request/response examples and error examples
- API key authentication (header `X-API-KEY`) and support for `X-RapidAPI-Key`
- Standardized JSON error payloads (`ApiErrorResponse`) for 400/401/500 responses
- Unit and integration tests (MockMvc + Spring Boot Test)

## Quick Start

Prerequisites:
- Java 17+
- Maven (or use your CI/build system)

Build and run locally:

```bash
git clone <repo-url>
cd CO2_API
mvn clean package
mvn spring-boot:run
```

Default server: `http://localhost:8080`

Set a production API key before running (PowerShell):

```powershell
$env:API_KEY = "your-production-key"
mvn spring-boot:run
```

Or run the packaged jar:

```bash
java -jar target/co2-api-1.0.0.jar
```

## API: Calculate Emissions

POST /api/v1/calculate

- Headers:
  - `Content-Type: application/json`
  - `X-API-KEY: <your-key>` (or `X-RapidAPI-Key` for RapidAPI)

Request body (JSON):

```json
{
  "weightKg": 5000.0,
  "distanceKm": 800.0,
  "transportType": "DIESEL_TRUCK"
}
```

Successful response (200):

```json
{
  "transportType": "DIESEL_TRUCK",
  "weightKg": 5000.0,
  "distanceKm": 800.0,
  "totalCo2Kg": 440.0,
  "emissionFactor": 0.11
}
```

Error responses use a consistent JSON structure. Example 400/401/500:

```json
{
  "timestamp": "2026-05-06T12:34:56.789Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for the request body",
  "path": "/api/v1/calculate",
  "details": [
    "weightKg Weight must be at least 1 kg",
    "transportType Transport type is required"
  ]
}
```

## OpenAPI / Swagger

Interactive docs: `http://localhost:8080/swagger-ui.html`
Raw spec: `http://localhost:8080/v3/api-docs`

The OpenAPI configuration includes example payloads for success and common errors, and documents the API key security scheme used by the interceptor.

## Emission Factors

Emission factors are defined in the `TransportType` enum (kg CO₂ per ton·km). Formula:

`CO₂ (kg) = (weightKg / 1000) * distanceKm * emissionFactor`

Common factors in this project:
- `DIESEL_TRUCK`: 0.11
- `ELECTRIC_TRUCK`: 0.03
- `TRAIN`: 0.02
- `FLIGHT`: 0.50
- `SHIP`: 0.01

## RapidAPI considerations

- The API supports `X-RapidAPI-Key` in addition to `X-API-KEY` to work more smoothly behind RapidAPI's proxy.
- Error responses are predictable JSON which helps RapidAPI consumers parse and display errors.
- Provide a clear example request/response and include usage notes when creating the RapidAPI listing.

## Docker

Build and run with Docker:

```dockerfile
FROM eclipse-temurin:17-jre-jammy
COPY target/co2-api-1.0.0.jar app.jar
ENV API_KEY=changeme-replace-in-production
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Tests

Run unit and integration tests:

```bash
mvn test
```

## Contributing / Extending

- Add new transport types in `TransportType.java` with a factor.
- Persist calculation results by adding a JPA entity and repository and saving in `EmissionService`.
- Add rate limiting via a new `HandlerInterceptor` registered in `WebConfig`.
- Add per-customer API keys and quotas for RapidAPI or production readiness.

---

If you want, I can also prepare a short “RapidAPI listing” description and suggested example snippets tailored to the RapidAPI UI. 
