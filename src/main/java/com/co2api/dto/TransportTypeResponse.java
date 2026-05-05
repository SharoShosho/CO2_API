package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a single transport type with its metadata.
 *
 * Returned as part of the GET /api/v1/transport-types response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Metadata for a supported transport type")
public class TransportTypeResponse {

    /** Enum name of the transport type (e.g. DIESEL_TRUCK). */
    @Schema(description = "Transport type identifier", example = "DIESEL_TRUCK")
    private String type;

    /** CO2 emission factor in kg CO2 per ton-kilometre. */
    @Schema(description = "CO2 emission factor (kg CO2 per ton-km)", example = "0.11")
    private double emissionFactor;

    /** Unit of the emission factor. Always 'kgCO2_per_tkm'. */
    @Schema(description = "Unit of the emission factor", example = "kgCO2_per_tkm")
    private String unit;

    /** Human-readable description of the transport type. */
    @Schema(description = "Human-readable description of the transport type",
            example = "Standard diesel-powered road freight truck")
    private String description;
}
