# CO2 Emission API

REST API for calculating CO₂ emissions for freight shipments. Built with Java 17 + Spring Boot 3. Published via [RapidAPI](https://rapidapi.com).

**Version:** 1.0.4

## What's included

- Calculate CO₂ for a single shipment (weight, distance, transport type)
- Batch calculation for up to 100 shipments in one request
- Compare two shipping options (A vs B)
- Find the lowest-emission transport type for a given shipment
- Calculate CO₂ with a custom emission factor
- Catalog of all transport types with emission factors
- Interactive OpenAPI/Swagger documentation
- API key authentication (`X-API-KEY`) with support for `X-RapidAPI-Key`
- Standardized JSON error responses (`ApiErrorResponse`) for 400/401/500
- Unit and integration tests (MockMvc + Spring Boot Test)

## Quick start

Requirements:
- Java 17+
- Maven

Build and run locally:

```bash
git clone <repo-url>
cd CO2_API
mvn clean package
mvn spring-boot:run
```

Default server: `http://localhost:8080`

Set a production API key (PowerShell):

```powershell
$env:API_KEY = "your-secret-key"
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
API_KEY=your-secret-key java -jar target/co2-api-1.0.2.jar
```

## All endpoints

All endpoints require the header `X-API-KEY: <your-key>`.

| Method | Path | Description |
|-------|--------|-------------|
| `POST` | `/api/v1/calculate` | Calculate CO₂ for a single shipment |
| `POST` | `/api/v1/calculate/batch` | Calculate CO₂ for multiple shipments |
| `POST` | `/api/v1/compare` | Compare two shipping options |
| `POST` | `/api/v1/compare/best` | Find the best transport type for a shipment |
| `POST` | `/api/v1/estimate` | Calculate CO₂ with a custom emission factor |
| `GET` | `/api/v1/transport-types` | List all transport types |
| `GET` | `/api/v1/transport-types/{code}` | Get a specific transport type |

### POST /api/v1/calculate

Calculate CO₂ for a single shipment.

```json
// Request
{
  "weightKg": 5000.0,
  "distanceKm": 800.0,
  "transportType": "DIESEL_TRUCK"
}

// Response 200
{
  "transportType": "DIESEL_TRUCK",
  "weightKg": 5000.0,
  "distanceKm": 800.0,
  "totalCo2Kg": 440.0,
  "emissionFactor": 0.11
}
```

### POST /api/v1/calculate/batch

Calculate CO₂ for up to 100 shipments in one request.

```json
// Request
{
  "shipments": [
    { "weightKg": 5000.0, "distanceKm": 800.0, "transportType": "DIESEL_TRUCK" },
    { "weightKg": 1200.0, "distanceKm": 300.0, "transportType": "TRAIN" }
  ]
}

// Response 200
{
  "results": [ ... ],
  "totalCo2Kg": 447.2
}
```

### POST /api/v1/compare

Compare two shipping options and see which one produces the lowest CO₂.

```json
// Request
{
  "optionA": { "weightKg": 5000.0, "distanceKm": 800.0, "transportType": "DIESEL_TRUCK" },
  "optionB": { "weightKg": 5000.0, "distanceKm": 800.0, "transportType": "TRAIN" }
}

// Response 200
{
  "optionA": { "totalCo2Kg": 440.0 },
  "optionB": { "totalCo2Kg": 80.0 },
  "differenceCo2Kg": 360.0,
  "lowerEmissionOption": "OPTION_B",
  "summary": "Option B has 360.0 kg lower CO2 than option A."
}
```

### POST /api/v1/compare/best

Find the greenest transport type — returns all options ranked from lowest to highest CO₂.

```json
// Request
{ "weightKg": 5000.0, "distanceKm": 800.0 }

// Response 200
{
  "best": { "transportType": "SHIP", "totalCo2Kg": 40.0, "emissionFactor": 0.01 },
  "ranked": [
    { "transportType": "SHIP",           "totalCo2Kg": 40.0  },
    { "transportType": "TRAIN",          "totalCo2Kg": 80.0  },
    { "transportType": "ELECTRIC_TRUCK", "totalCo2Kg": 120.0 },
    { "transportType": "DIESEL_TRUCK",   "totalCo2Kg": 440.0 },
    { "transportType": "FLIGHT",         "totalCo2Kg": 2000.0 }
  ]
}
```

### POST /api/v1/estimate

Calculate CO₂ with a custom emission factor (for example, a certified value).

```json
// Request
{ "weightKg": 5000.0, "distanceKm": 800.0, "customFactorKgPerTonKm": 0.08 }

// Response 200
{ "weightKg": 5000.0, "distanceKm": 800.0, "customFactorKgPerTonKm": 0.08, "totalCo2Kg": 320.0 }
```

### GET /api/v1/transport-types

List all transport types with emission factors and descriptions.

### GET /api/v1/transport-types/{code}

Get a specific transport type, for example `GET /api/v1/transport-types/TRAIN`.

## Error handling

All errors are returned as `ApiErrorResponse`:

```json
{
  "timestamp": "2026-05-06T12:34:56.789Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for the request body",
  "path": "/api/v1/calculate",
  "details": ["weightKg Weight must be at least 1 kg"]
}
```

| Status | Description |
|--------|-------------|
| 400 | Validation error or invalid JSON |
| 401 | Missing or invalid API key |
| 500 | Unexpected server error |

## Emission factors (formula)

`CO₂ (kg) = (weightKg / 1000) × distanceKm × emissionFactor`

Factors are defined in the `TransportType` enum (kg CO₂ per ton·km):

| Code | Factor | Description |
|-----|--------|-------------|
| `DIESEL_TRUCK` | 0.11 | Standard diesel freight truck |
| `ELECTRIC_TRUCK` | 0.03 | Electric truck |
| `TRAIN` | 0.02 | Freight train — one of the most efficient options |
| `FLIGHT` | 0.50 | Air freight — highest emission intensity |
| `SHIP` | 0.01 | Container ship — efficient for large volumes |

## OpenAPI / Swagger

Interactive documentation: `http://localhost:8080/swagger-ui.html`  
Raw spec (JSON): `http://localhost:8080/v3/api-docs`

## Docker

```dockerfile
FROM eclipse-temurin:17-jre-jammy
COPY target/co2-api-1.0.2.jar app.jar
ENV API_KEY=changeme-replace-in-production
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Tests

```bash
mvn test
```

## Extending the project

The project is structured to make it easy to extend:

- **New transport type** — Add a new constant in `TransportType.java` with a factor and description. No other files need to change.
- **New API version (v2)** — Create controllers with the `ApiConstants.V2` prefix and add a `v2Api()` bean in `SwaggerConfig.java`.
- **Database** — Add a JPA entity and repository, then store results in `EmissionService`.
- **Rate limiting** — Add a new `HandlerInterceptor` and register it in `WebConfig.java`.
- **New endpoint** — Create a new controller in `com.co2api.controller`, use the `ApiConstants.V1` prefix, and add business logic in `EmissionService`.

## RapidAPI

- The API supports `X-RapidAPI-Key` in addition to `X-API-KEY` (handled in `ApiKeyInterceptor`).
- All error responses are predictable JSON objects, which makes it easier for RapidAPI consumers to parse and display errors.
