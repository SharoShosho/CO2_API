package com.co2api.controller;

import com.co2api.config.ApiConstants;
import com.co2api.dto.ApiErrorResponse;
import com.co2api.dto.EstimateRequest;
import com.co2api.dto.EstimateResponse;
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
 * REST controller for CO2 estimation with a custom emission factor.
 *
 * <p>Use this endpoint when you have your own certified or company-specific
 * emission factor and do not want to rely on the built-in {@code TransportType} values.
 *
 * Base path: /api/v1/estimate
 */
@RestController
@RequestMapping(ApiConstants.V1 + "/estimate")
@RequiredArgsConstructor
@Tag(name = "Custom Estimate", description = "Estimate CO2 emissions using a caller-supplied emission factor")
public class EstimateController {

    private final EmissionService emissionService;

    @Operation(
            summary = "Estimate CO2 with a custom emission factor",
            description = "Provide shipment weight (kg), distance (km), and your own emission factor "
                    + "(kg CO2 / t·km) to receive the estimated CO2 in kg.",
            security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estimate calculated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstimateResponse.class),
                            examples = {@ExampleObject(
                                    name = "Custom factor example",
                                    summary = "5 t × 800 km × 0.08 = 320 kg CO2",
                                    value = """
                                            {
                                              "weightKg": 5000.0,
                                              "distanceKm": 800.0,
                                              "customFactorKgPerTonKm": 0.08,
                                              "totalCo2Kg": 320.0
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
    public ResponseEntity<EstimateResponse> estimate(
            @Parameter(description = "Shipment data with custom emission factor", required = true)
            @Valid @RequestBody EstimateRequest request) {
        return ResponseEntity.ok(emissionService.estimateCustom(request));
    }
}
