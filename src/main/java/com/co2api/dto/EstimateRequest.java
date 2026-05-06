package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request payload for the {@code POST /api/v1/estimate} endpoint.
 *
 * Allows callers to supply a custom emission factor instead of using one
 * of the predefined {@link com.co2api.enums.TransportType} values. Useful
 * when the client holds a certified or company-specific factor.
 */
@Data
@Schema(
        description = "Request payload for estimating CO2 with a custom emission factor.",
        example = "{\"weightKg\":5000,\"distanceKm\":800,\"customFactorKgPerTonKm\":0.08}"
)
public class EstimateRequest {

    /** Weight of the shipment in kilograms. Must be at least 1. */
    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1 kg")
    @Schema(description = "Weight of the shipment in kilograms", example = "5000.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double weightKg;

    /** Distance of the shipment in kilometres. Must be at least 1. */
    @NotNull(message = "Distance is required")
    @Min(value = 1, message = "Distance must be at least 1 km")
    @Schema(description = "Distance of the shipment in kilometres", example = "800.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distanceKm;

    /**
     * Custom emission factor expressed as kg CO₂ per ton-kilometre.
     * Must be greater than zero.
     */
    @NotNull(message = "Custom emission factor is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Custom emission factor must be greater than 0")
    @Schema(
            description = "Custom emission factor in kg CO2 per ton-km",
            example = "0.08",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double customFactorKgPerTonKm;
}
