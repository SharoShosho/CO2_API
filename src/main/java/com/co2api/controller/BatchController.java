package com.co2api.controller;

import com.co2api.config.ApiConstants;
import com.co2api.dto.ApiErrorResponse;
import com.co2api.dto.BatchRequest;
import com.co2api.dto.BatchResponse;
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
 * REST controller for batch CO2 emission calculations.
 *
 * Base path: /api/v1/calculate/batch
 */
@RestController
@RequestMapping(ApiConstants.V1 + "/calculate/batch")
@RequiredArgsConstructor
@Tag(name = "Batch CO2 Calculation", description = "Calculate CO2 for multiple shipments in one request")
public class BatchController {

    private final EmissionService emissionService;

    @Operation(
            summary = "Calculate CO2 emissions for a batch of shipments",
            description = "Provide a list of shipments and receive one emission result per shipment plus the total CO2.",
            security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Batch calculated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BatchResponse.class),
                            examples = {@ExampleObject(
                                    name = "Batch example",
                                    summary = "Two shipments in one request",
                                    value = """
                                            {
                                              "results": [
                                                {
                                                  "transportType": "TRAIN",
                                                  "weightKg": 5000.0,
                                                  "distanceKm": 800.0,
                                                  "totalCo2Kg": 80.0,
                                                  "emissionFactor": 0.02
                                                },
                                                {
                                                  "transportType": "DIESEL_TRUCK",
                                                  "weightKg": 5000.0,
                                                  "distanceKm": 800.0,
                                                  "totalCo2Kg": 440.0,
                                                  "emissionFactor": 0.11
                                                }
                                              ],
                                              "totalCo2Kg": 520.0
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid batch request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid API key",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<BatchResponse> calculateBatch(
            @Parameter(description = "Batch of shipment requests", required = true)
            @Valid @RequestBody BatchRequest request) {
        return ResponseEntity.ok(emissionService.calculateBatch(request));
    }
}

