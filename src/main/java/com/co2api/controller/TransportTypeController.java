package com.co2api.controller;

import com.co2api.dto.ApiErrorResponse;
import com.co2api.dto.TransportTypeResponse;
import com.co2api.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
 * REST controller exposing supported transport types.
 */
@RestController
@RequestMapping("/api/v1/transport-types")
@RequiredArgsConstructor
@Tag(name = "Transport Types", description = "List supported transport types and emission factors")
public class TransportTypeController {

    private final EmissionService emissionService;

    @Operation(
            summary = "List all supported transport types",
            description = "Returns the transport types supported by the API together with their emission factors.",
            security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transport types returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransportTypeResponse.class),
                            examples = {@ExampleObject(
                                    name = "Transport types example",
                                    summary = "Supported transport types",
                                    value = """
                                            [
                                              {"code":"DIESEL_TRUCK","emissionFactor":0.11,"unit":"kg CO2 / t·km"},
                                              {"code":"ELECTRIC_TRUCK","emissionFactor":0.03,"unit":"kg CO2 / t·km"},
                                              {"code":"TRAIN","emissionFactor":0.02,"unit":"kg CO2 / t·km"},
                                              {"code":"FLIGHT","emissionFactor":0.50,"unit":"kg CO2 / t·km"},
                                              {"code":"SHIP","emissionFactor":0.01,"unit":"kg CO2 / t·km"}
                                            ]
                                            """
                            )}
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
    @GetMapping
    public ResponseEntity<List<TransportTypeResponse>> getTransportTypes() {
        return ResponseEntity.ok(emissionService.getAllTransportTypes());
    }
}

