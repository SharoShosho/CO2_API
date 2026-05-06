package com.co2api.controller;

import com.co2api.config.ApiConstants;
import com.co2api.dto.ApiErrorResponse;
import com.co2api.dto.BestRouteRequest;
import com.co2api.dto.BestRouteResponse;
import com.co2api.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
 * REST controller for finding the lowest-emission transport option.
 *
 * <p>Calculates CO2 for every supported transport type and returns them
 * ranked from lowest to highest so callers can pick the greenest option.
 *
 * Base path: /api/v1/compare/best
 */
@RestController
@RequestMapping(ApiConstants.V1 + "/compare/best")
@RequiredArgsConstructor
@Tag(name = "CO2 Comparison", description = "Compare two shipment emission calculations")
public class BestRouteController {

    private final EmissionService emissionService;

    @Operation(
            summary = "Find the lowest-emission transport type for a shipment",
            description = "Provide shipment weight and distance and receive all transport options "
                    + "ranked from lowest to highest CO2 emission.",
            security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ranked list returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BestRouteResponse.class),
                            examples = {@ExampleObject(
                                    name = "Best route example",
                                    summary = "SHIP is best for 5000 kg / 800 km",
                                    value = """
                                            {
                                              "best": {
                                                "transportType": "SHIP",
                                                "weightKg": 5000.0,
                                                "distanceKm": 800.0,
                                                "totalCo2Kg": 40.0,
                                                "emissionFactor": 0.01
                                              },
                                              "ranked": [
                                                {"transportType":"SHIP","weightKg":5000.0,"distanceKm":800.0,"totalCo2Kg":40.0,"emissionFactor":0.01},
                                                {"transportType":"TRAIN","weightKg":5000.0,"distanceKm":800.0,"totalCo2Kg":80.0,"emissionFactor":0.02},
                                                {"transportType":"ELECTRIC_TRUCK","weightKg":5000.0,"distanceKm":800.0,"totalCo2Kg":120.0,"emissionFactor":0.03},
                                                {"transportType":"DIESEL_TRUCK","weightKg":5000.0,"distanceKm":800.0,"totalCo2Kg":440.0,"emissionFactor":0.11},
                                                {"transportType":"FLIGHT","weightKg":5000.0,"distanceKm":800.0,"totalCo2Kg":2000.0,"emissionFactor":0.50}
                                              ]
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid API key",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<BestRouteResponse> findBestRoute(
            @Parameter(description = "Shipment weight and distance", required = true)
            @Valid @RequestBody BestRouteRequest request) {
        return ResponseEntity.ok(emissionService.findBestRoute(request));
    }
}
