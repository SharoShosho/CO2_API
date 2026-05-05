package com.co2api.controller;

import com.co2api.dto.ApiErrorResponse;
import com.co2api.dto.EmissionResponse;
import com.co2api.dto.ShipmentRequest;
import com.co2api.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the CO2 emission calculation endpoint.
 *
 * Base path: /api/v1/calculate
 *
 * All endpoints require a valid X-API-KEY header (enforced by ApiKeyInterceptor).
 * The @RequiredArgsConstructor annotation generates a constructor for final fields,
 * enabling constructor-based dependency injection of EmissionService.
 */
@RestController
@RequestMapping("/api/v1/calculate")
@RequiredArgsConstructor
@Tag(name = "CO2 Emission Calculation", description = "Endpoints for calculating CO2 emissions from shipments")
public class CalculationController {

    private static final String SUCCESS_EXAMPLE = "{\n"
            + "  \"transportType\": \"DIESEL_TRUCK\",\n"
            + "  \"weightKg\": 5000.0,\n"
            + "  \"distanceKm\": 800.0,\n"
            + "  \"totalCo2Kg\": 440.0,\n"
            + "  \"emissionFactor\": 0.11\n"
            + "}";

    private static final String VALIDATION_ERROR_EXAMPLE = "{\n"
            + "  \"timestamp\": \"2026-05-06T12:34:56.789Z\",\n"
            + "  \"status\": 400,\n"
            + "  \"error\": \"Bad Request\",\n"
            + "  \"message\": \"Validation failed for the request body\",\n"
            + "  \"path\": \"/api/v1/calculate\",\n"
            + "  \"details\": [\n"
            + "    \"weightKg Weight must be at least 1 kg\",\n"
            + "    \"transportType Transport type is required\"\n"
            + "  ]\n"
            + "}";

    private static final String UNAUTHORIZED_ERROR_EXAMPLE = "{\n"
            + "  \"timestamp\": \"2026-05-06T12:34:56.789Z\",\n"
            + "  \"status\": 401,\n"
            + "  \"error\": \"Unauthorized\",\n"
            + "  \"message\": \"Missing or invalid API key\",\n"
            + "  \"path\": \"/api/v1/calculate\",\n"
            + "  \"details\": []\n"
            + "}";

    private static final String SERVER_ERROR_EXAMPLE = "{\n"
            + "  \"timestamp\": \"2026-05-06T12:34:56.789Z\",\n"
            + "  \"status\": 500,\n"
            + "  \"error\": \"Internal Server Error\",\n"
            + "  \"message\": \"An unexpected error occurred\",\n"
            + "  \"path\": \"/api/v1/calculate\",\n"
            + "  \"details\": [\"No additional details available\"]\n"
            + "}";

    /** Service layer that contains the emission calculation business logic. */
    private final EmissionService emissionService;

    /**
     * Calculates the total CO2 emission for a shipment.
     *
     * Accepts a JSON body with weight (kg), distance (km) and transport type.
     * Returns the total CO2 emission in kilograms together with calculation details.
     *
     * @param request validated request body containing shipment data
     * @return 200 OK with an EmissionResponse JSON payload
     */
    @Operation(
            summary = "Calculate CO2 emissions for a shipment",
            description = "Provide shipment weight (kg), distance (km), and transport type to receive the estimated CO2 emission in kg. "
                    + "The endpoint accepts the standard X-API-KEY header and also supports X-RapidAPI-Key when published through RapidAPI.",
            security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Emission calculated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmissionResponse.class),
                            examples = {@ExampleObject(
                                    name = "Diesel truck example",
                                    summary = "Typical successful calculation",
                                    value = SUCCESS_EXAMPLE
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body, malformed JSON, or failed validation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {@ExampleObject(
                                    name = "Validation error",
                                    summary = "Invalid shipment payload",
                                    value = VALIDATION_ERROR_EXAMPLE
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid API key",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {@ExampleObject(
                                    name = "Unauthorized error",
                                    summary = "API key missing or invalid",
                                    value = UNAUTHORIZED_ERROR_EXAMPLE
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {@ExampleObject(
                                    name = "Server error",
                                    summary = "Unexpected exception",
                                    value = SERVER_ERROR_EXAMPLE
                            )}
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EmissionResponse> calculate(
            @Parameter(description = "Shipment details for emission calculation", required = true)
            @Valid @RequestBody ShipmentRequest request) {

        // Delegate the calculation to the service layer and wrap the result in a 200 OK response
        EmissionResponse response = emissionService.calculateEmissions(request);
        return ResponseEntity.ok(response);
    }
}
