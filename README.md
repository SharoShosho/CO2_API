# CO2 Emission Calculation API

A scalable and robust REST API built with **Java 17** and **Spring Boot 3** that estimates the CO₂ emissions produced by freight shipments. Clients provide the shipment weight, distance, and mode of transport; the API returns the total CO₂ in kilograms together with the emission factor used.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Transport Types & Emission Factors](#transport-types--emission-factors)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Build & Run](#build--run)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [API Usage](#api-usage)
  - [Authentication](#authentication)
  - [Calculate Emissions Endpoint](#calculate-emissions-endpoint)
  - [Example Request](#example-request)
  - [Example Response](#example-response)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Extending the Project](#extending-the-project)

---

## Features

- 🚛 CO₂ calculation for multiple transport modes (truck, train, flight, ship)
- 🔐 API key authentication via `X-API-KEY` header (configurable interceptor)
- 📖 Interactive Swagger UI for testing endpoints in the browser
- ✅ Bean Validation on all request fields
- 🗄️ Spring Data JPA + H2 in-memory database (ready for PostgreSQL / MySQL swap)
- 🧪 Integration and unit tests with Spring Boot Test & MockMvc

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Programming language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Web | — | REST controllers |
| Spring Data JPA | — | ORM / database abstraction |
| H2 Database | — | In-memory database (dev/test) |
| Lombok | — | Boilerplate reduction |
| Springdoc OpenAPI | 2.5.0 | Swagger UI & OpenAPI 3 docs |
| Maven | 3.x | Build tool |

---

## Project Structure

```
co2-api/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/co2api/
    │   │   ├── Co2ApiApplication.java          # Application entry point
    │   │   ├── config/
    │   │   │   ├── SwaggerConfig.java           # OpenAPI / Swagger UI setup
    │   │   │   └── WebConfig.java              # MVC interceptor registration
    │   │   ├── controller/
    │   │   │   └── CalculationController.java  # POST /api/v1/calculate
    │   │   ├── dto/
    │   │   │   ├── ShipmentRequest.java        # Incoming request body
    │   │   │   └── EmissionResponse.java       # Outgoing response body
    │   │   ├── enums/
    │   │   │   └── TransportType.java          # Transport modes + emission factors
    │   │   ├── security/
    │   │   │   └── ApiKeyInterceptor.java      # X-API-KEY validation
    │   │   └── service/
    │   │       └── EmissionService.java        # CO₂ calculation logic
    │   └── resources/
    │       └── application.properties          # App configuration
    └── test/
        └── java/com/co2api/
            └── Co2ApiApplicationTests.java     # Integration & unit tests
```

---

## Transport Types & Emission Factors

Emission factors are expressed in **kg CO₂ per ton-kilometre (kg CO₂ / t·km)**.

| Transport Type | Enum Value | Factor (kg CO₂/t·km) |
|---|---|---|
| Diesel Truck | `DIESEL_TRUCK` | 0.11 |
| Electric Truck | `ELECTRIC_TRUCK` | 0.03 |
| Train | `TRAIN` | 0.02 |
| Cargo Flight | `FLIGHT` | 0.50 |
| Cargo Ship | `SHIP` | 0.01 |

The formula used is:

```
CO₂ (kg) = (weight_kg / 1000) × distance_km × emission_factor
```

---

## Getting Started

### Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)

### Build & Run

```bash
# Clone the repository
git clone https://github.com/SharoShosho/CO2_API.git
cd CO2_API

# Build and run tests
mvn clean package

# Start the application
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

To set a custom API key at startup:

```bash
API_KEY=my-secret-key mvn spring-boot:run
```

---

## API Documentation (Swagger)

Once the application is running, open **http://localhost:8080/swagger-ui.html** in your browser.

Click the **Authorize** button (top right) and enter your API key to authenticate all subsequent requests directly in the UI.

The raw OpenAPI 3 JSON spec is available at **http://localhost:8080/v3/api-docs**.

---

## API Usage

### Authentication

Every request to `/api/v1/**` must include the `X-API-KEY` header.

```
X-API-KEY: changeme-replace-in-production
```

Requests without a valid key receive a `401 Unauthorized` response.

Swagger UI paths (`/swagger-ui/**`, `/v3/api-docs/**`) are publicly accessible without a key.

---

### Calculate Emissions Endpoint

| Property | Value |
|---|---|
| **Method** | `POST` |
| **Path** | `/api/v1/calculate` |
| **Content-Type** | `application/json` |
| **Authorization** | `X-API-KEY` header |

**Request body fields:**

| Field | Type | Required | Description |
|---|---|---|---|
| `weightKg` | number | ✅ | Shipment weight in kilograms (min 1) |
| `distanceKm` | number | ✅ | Shipment distance in kilometres (min 1) |
| `transportType` | string (enum) | ✅ | One of: `DIESEL_TRUCK`, `ELECTRIC_TRUCK`, `TRAIN`, `FLIGHT`, `SHIP` |

---

### Example Request

```bash
curl -X POST http://localhost:8080/api/v1/calculate \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: changeme-replace-in-production" \
  -d '{
    "weightKg": 5000,
    "distanceKm": 800,
    "transportType": "DIESEL_TRUCK"
  }'
```

### Example Response

```json
{
  "transportType": "DIESEL_TRUCK",
  "weightKg": 5000.0,
  "distanceKm": 800.0,
  "totalCo2Kg": 440.0,
  "emissionFactor": 0.11
}
```

**Calculation breakdown:**
```
5000 kg / 1000 = 5 tons
5 tons × 800 km × 0.11 (kg CO₂/t·km) = 440 kg CO₂
```

---

## Configuration

All settings are managed in `src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `api.key` | `changeme-replace-in-production` | Valid API key (set via `API_KEY` env var in production) |
| `spring.h2.console.enabled` | `true` | Enable H2 browser console at `/h2-console` |
| `springdoc.swagger-ui.enabled` | `true` | Enable Swagger UI |

**Production deployment:** Set the `API_KEY` environment variable to a strong secret and disable the H2 console (`spring.h2.console.enabled=false`).

---

## Running Tests

```bash
mvn test
```

The test suite covers:

- ✅ CO₂ calculation correctness for each transport type
- ✅ `POST /api/v1/calculate` returns `200` with a valid API key
- ✅ `POST /api/v1/calculate` returns `401` with a missing or wrong API key
- ✅ `POST /api/v1/calculate` returns `400` for an invalid request body
- ✅ Spring application context loads successfully

---

## Extending the Project

The project is designed to be easy to extend:

| Goal | How |
|---|---|
| **Add a new transport type** | Add a constant to `TransportType.java` with the emission factor |
| **Persist calculation history** | Add a JPA `@Entity` and `JpaRepository`; call `.save()` in `EmissionService` |
| **Switch to PostgreSQL** | Replace the H2 dependency with the PostgreSQL driver in `pom.xml` and update `application.properties` |
| **Add rate limiting** | Create a new `HandlerInterceptor` and register it in `WebConfig` |
| **Add user management** | Integrate Spring Security with JWT or OAuth 2.0 alongside the existing interceptor |