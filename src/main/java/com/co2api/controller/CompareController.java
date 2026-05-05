package com.co2api.controller;

import com.co2api.dto.ApiErrorResponse;
import com.co2api.dto.CompareRequest;
import com.co2api.dto.CompareResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for comparing two shipment emission calculations.
 */
@RestController
@RequestMapping("/api/v1/compare")
@RequiredArgsConstructor
@Tag(name = "CO2 Comparison", description = "Compare two shipment emission calculations")
public class CompareController {

    private final EmissionService emissionService;

    @Operation(
            summary = "Compare two shipment emission calculations",
            description = "Provide two shipment options and receive both calculated emissions plus a comparison result.",
            security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comparison completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompareResponse.class),
                            examples = {@ExampleObject(
                                    name = "Compare example",
                                    summary = "Option A is lower than option B",
                                    value = """
                                            {
                                              "optionA": {
                                                "transportType": "TRAIN",
                                                "weightKg": 5000.0,
                                                "distanceKm": 800.0,
                                                "totalCo2Kg": 80.0,
                                                "emissionFactor": 0.02
                                              },
                                              "optionB": {
                                                "transportType": "DIESEL_TRUCK",
                                                "weightKg": 5000.0,
                                                "distanceKm": 800.0,
                                                "totalCo2Kg": 440.0,
                                                "emissionFactor": 0.11
                                              },
                                              "differenceCo2Kg": 360.0,
                                              "lowerEmissionOption": "OPTION_A",
                                              "summary": "Option A has 360.0 kg lower CO2 than option B."
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid compare request body",
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
    @PutMapping
    public ResponseEntity<CompareResponse> compare(
            @Parameter(description = "Two shipment options to compare", required = true)
            @Valid @RequestBody CompareRequest request) {
        return ResponseEntity.ok(emissionService.compareEmissions(request));
    }
}
