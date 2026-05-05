package com.co2api.controller;

import com.co2api.dto.CompareRequest;
import com.co2api.dto.CompareResponse;
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
 * REST controller that exposes the CO2 emission comparison endpoint.
 *
 * Base path: /api/v1/compare
 *
 * All endpoints require a valid X-API-KEY header (enforced by ApiKeyInterceptor).
 */
@RestController
@RequestMapping("/api/v1/compare")
@RequiredArgsConstructor
@Tag(name = "CO2 Emission Comparison", description = "Endpoint for comparing CO2 emissions of two shipment options")
public class CompareController {

    private final EmissionService emissionService;

    /**
     * Compares CO2 emissions for two shipment options (A and B).
     *
     * Returns the individual emission results for each option along with
     * the absolute difference (kg), relative difference (%), and which option
     * is the better (lower-emission) choice.
     *
     * @param compareRequest validated request body containing optionA and optionB
     * @return 200 OK with a CompareResponse
     */
    @Operation(
        summary = "Compare CO2 emissions of two shipment options",
        description = "Provide two shipment options (A and B) to compare their CO2 emissions side by side. " +
                      "The response includes the emission result for each option, the absolute and percentage difference, " +
                      "and indicates which option produces fewer emissions.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comparison completed successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CompareResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request body (validation error)"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    @PostMapping
    public ResponseEntity<CompareResponse> compare(
            @Parameter(description = "Two shipment options to compare", required = true)
            @Valid @RequestBody CompareRequest compareRequest) {

        CompareResponse response = emissionService.compareEmissions(compareRequest);
        return ResponseEntity.ok(response);
    }
}
