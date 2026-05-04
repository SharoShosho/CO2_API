package com.co2api.controller;

import com.co2api.dto.TransportTypeResponse;
import com.co2api.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes the transport types metadata endpoint.
 *
 * Base path: /api/v1/transport-types
 *
 * All endpoints require a valid X-API-KEY header (enforced by ApiKeyInterceptor).
 */
@RestController
@RequestMapping("/api/v1/transport-types")
@RequiredArgsConstructor
@Tag(name = "Transport Types", description = "Endpoint for retrieving supported transport types and their emission factors")
public class TransportTypeController {

    private final EmissionService emissionService;

    /**
     * Returns a list of all supported transport types with their emission factors,
     * units, and descriptions.
     *
     * Useful for building dynamic dropdowns or validating transport type values
     * on the client side without hardcoding enum names.
     *
     * @return 200 OK with an array of TransportTypeResponse objects
     */
    @Operation(
        summary = "List all supported transport types",
        description = "Returns all transport types supported by the API, including their CO2 emission factors (kg CO2 per ton-km) and descriptions.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transport types retrieved successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = TransportTypeResponse.class)))
        ),
        @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY header")
    })
    @GetMapping
    public ResponseEntity<List<TransportTypeResponse>> getTransportTypes() {
        return ResponseEntity.ok(emissionService.getAllTransportTypes());
    }
}
