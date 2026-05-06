package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request payload for the {@code POST /api/v1/compare/best} endpoint.
 *
 * Provide a shipment weight and distance and the API will calculate the CO₂
 * emission for every supported transport type and return them ranked from
 * lowest to highest emission.
 */
@Data
@Schema(
        description = "Shipment parameters used to find the best (lowest-emission) transport type.",
        example = "{\"weightKg\":5000,\"distanceKm\":800}"
)
public class BestRouteRequest {

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
}
