# CO2 Emission API

REST API för att beräkna CO₂-utsläpp för fraktleveranser. Byggt med Java 17 + Spring Boot 3. Publiceras via [RapidAPI](https://rapidapi.com).

**Version:** 1.0.4

## Vad ingår

- Beräkna CO₂ för ett enskilt frakt (vikt, avstånd, transporttyp)
- Batch-beräkning för upp till 100 frakter i ett anrop
- Jämför två fraktalternativ (A vs B)
- Hitta den lägsta-utsläpps-transporttypen för ett givet frakt
- Beräkna CO₂ med en egen (anpassad) emissionsfaktor
- Katalog av alla transporttyper med emissionsfaktorer
- Interaktiv OpenAPI/Swagger-dokumentation
- API-nyckelautentisering (`X-API-KEY`) med stöd för `X-RapidAPI-Key`
- Standardiserade JSON-felmeddelanden (`ApiErrorResponse`) för 400/401/500
- Enhets- och integrationstester (MockMvc + Spring Boot Test)

## Snabbstart

Krav:
- Java 17+
- Maven

Bygg och starta lokalt:

```bash
git clone <repo-url>
cd CO2_API
mvn clean package
mvn spring-boot:run
```

Standard-server: `http://localhost:8080`

Sätt en produktions-API-nyckel (PowerShell):

```powershell
$env:API_KEY = "din-hemliga-nyckel"
mvn spring-boot:run
```

Eller kör den packade jar-filen:

```bash
API_KEY=din-hemliga-nyckel java -jar target/co2-api-1.0.4.jar
```

## Alla endpoints

Alla endpoints kräver headern `X-API-KEY: <din-nyckel>`.

| Metod | Sökväg | Beskrivning |
|-------|--------|-------------|
| `POST` | `/api/v1/calculate` | Beräkna CO₂ för ett frakt |
| `POST` | `/api/v1/calculate/batch` | Beräkna CO₂ för flera frakter |
| `POST` | `/api/v1/compare` | Jämför två fraktalternativ |
| `POST` | `/api/v1/compare/best` | Hitta bästa transporttyp för ett frakt |
| `POST` | `/api/v1/estimate` | Beräkna CO₂ med egen emissionsfaktor |
| `GET` | `/api/v1/transport-types` | Lista alla transporttyper |
| `GET` | `/api/v1/transport-types/{code}` | Hämta en specifik transporttyp |

### POST /api/v1/calculate

Beräkna CO₂ för ett enskilt frakt.

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

Beräkna CO₂ för upp till 100 frakter i ett anrop.

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

Jämför två fraktalternativ och se vilket som ger lägst CO₂.

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

Hitta den grönaste transporttypen — returnerar alla alternativ rankade från lägst till högst CO₂.

```json
// Request
{ "weightKg": 5000.0, "distanceKm": 800.0 }

// Response 200
{
  "best": { "transportType": "SHIP", "totalCo2Kg": 40.0, "emissionFactor": 0.01 },
  "ranked": [
    { "transportType": "SHIP",          "totalCo2Kg": 40.0  },
    { "transportType": "TRAIN",         "totalCo2Kg": 80.0  },
    { "transportType": "ELECTRIC_TRUCK","totalCo2Kg": 120.0 },
    { "transportType": "DIESEL_TRUCK",  "totalCo2Kg": 440.0 },
    { "transportType": "FLIGHT",        "totalCo2Kg": 2000.0}
  ]
}
```

### POST /api/v1/estimate

Beräkna CO₂ med en egen emissionsfaktor (t.ex. ett certifierat värde).

```json
// Request
{ "weightKg": 5000.0, "distanceKm": 800.0, "customFactorKgPerTonKm": 0.08 }

// Response 200
{ "weightKg": 5000.0, "distanceKm": 800.0, "customFactorKgPerTonKm": 0.08, "totalCo2Kg": 320.0 }
```

### GET /api/v1/transport-types

Lista alla transporttyper med emissionsfaktorer och beskrivningar.

### GET /api/v1/transport-types/{code}

Hämta en specifik transporttyp, t.ex. `GET /api/v1/transport-types/TRAIN`.

## Felhantering

Alla fel returneras som `ApiErrorResponse`:

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

| Status | Beskrivning |
|--------|-------------|
| 400 | Valideringsfel eller felaktig JSON |
| 401 | Saknad eller ogiltig API-nyckel |
| 500 | Oväntade serverfel |

## Emissionsfaktorer (formel)

`CO₂ (kg) = (weightKg / 1000) × distanceKm × emissionFactor`

Faktorer definieras i `TransportType`-enum (kg CO₂ per ton·km):

| Kod | Faktor | Beskrivning |
|-----|--------|-------------|
| `DIESEL_TRUCK` | 0.11 | Standard diesellastbil för frakt |
| `ELECTRIC_TRUCK` | 0.03 | Elektrisk lastbil |
| `TRAIN` | 0.02 | Godståg — ett av de mest effektiva alternativen |
| `FLIGHT` | 0.50 | Flygfrakt — högst utsläppsintensitet |
| `SHIP` | 0.01 | Containerfartyg — effektivt för stora volymer |

## OpenAPI / Swagger

Interaktiv dokumentation: `http://localhost:8080/swagger-ui.html`  
Raw spec (JSON): `http://localhost:8080/v3/api-docs`

## Docker

```dockerfile
FROM eclipse-temurin:17-jre-jammy
COPY target/co2-api-1.0.4.jar app.jar
ENV API_KEY=changeme-replace-in-production
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Tester

```bash
mvn test
```

## Att bygga vidare

Projektet är strukturerat för att vara enkelt att utöka:

- **Ny transporttyp** — Lägg till en ny konstant i `TransportType.java` med faktor och beskrivning. Inga andra filer behöver ändras.
- **Ny API-version (v2)** — Skapa controllers med `ApiConstants.V2`-prefix och lägg till en `v2Api()` bean i `SwaggerConfig.java`.
- **Databas** — Lägg till JPA-entitet och repository, spara resultat i `EmissionService`.
- **Rate limiting** — Lägg till ett nytt `HandlerInterceptor` och registrera det i `WebConfig.java`.
- **Ny endpoint** — Skapa en ny controller i `com.co2api.controller`, använd `ApiConstants.V1` som prefix och lägg till affärslogiken i `EmissionService`.

## RapidAPI

- API:et stöder `X-RapidAPI-Key` utöver `X-API-KEY` (hanteras i `ApiKeyInterceptor`).
- Alla felresponser är förutsägbara JSON-objekt, vilket underlättar för RapidAPI-konsumenter att parsa och visa fel.
