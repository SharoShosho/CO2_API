package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned by the {@code POST /api/v1/estimate} endpoint.
 *
 * Echoes back the input values along with the calculated CO₂ total so that
 * clients can verify which factor was applied.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of a CO2 estimate using a custom emission factor.")
public class EstimateResponse {

    @Schema(description = "Weight of the shipment in kilograms", example = "5000.0")
    private double weightKg;

    @Schema(description = "Distance of the shipment in kilometres", example = "800.0")
    private double distanceKm;

    @Schema(description = "Custom emission factor applied (kg CO2 per ton-km)", example = "0.08")
    private double customFactorKgPerTonKm;

    @Schema(description = "Total CO2 emission in kilograms", example = "320.0")
    private double totalCo2Kg;
}
