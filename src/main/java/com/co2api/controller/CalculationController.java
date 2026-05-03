package com.co2api.controller;

import com.co2api.dto.EmissionResponse;
import com.co2api.dto.ShipmentRequest;
import com.co2api.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
        description = "Provide shipment weight (kg), distance (km), and transport type to receive the estimated CO2 emission in kg.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Emission calculated successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = EmissionResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request body (validation error)"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
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
