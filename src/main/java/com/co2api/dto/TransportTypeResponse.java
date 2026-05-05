package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight transport type view returned by the catalog endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transport type catalog entry")
public class TransportTypeResponse {

    @Schema(description = "Transport type code", example = "DIESEL_TRUCK")
    private String code;

    @Schema(description = "Emission factor in kg CO2 per ton-km", example = "0.11")
    private double emissionFactor;

    @Schema(description = "Measurement unit for the emission factor", example = "kg CO2 / t·km")
    private String unit;
}

