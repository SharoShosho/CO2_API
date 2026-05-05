package com.co2api.dto;

import com.co2api.enums.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for an incoming shipment emission calculation request.
 *
 * This class is used as the request body for the POST /api/v1/calculate endpoint.
 * Bean Validation annotations ensure that invalid data is rejected before reaching
 * the service layer.
 */
@Data
@Schema(
        description = "Request payload for calculating CO2 emissions for a shipment.",
        example = "{\"weightKg\":5000,\"distanceKm\":800,\"transportType\":\"DIESEL_TRUCK\"}"
)
public class ShipmentRequest {

    /**
     * Weight of the shipment in kilograms (kg).
     * Must be greater than 0.
     */
    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1 kg")
    @Schema(description = "Weight of the shipment in kilograms", example = "5000.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double weightKg;

    /**
     * Distance of the shipment in kilometres (km).
     * Must be greater than 0.
     */
    @NotNull(message = "Distance is required")
    @Min(value = 1, message = "Distance must be at least 1 km")
    @Schema(description = "Distance of the shipment in kilometres", example = "800.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distanceKm;

    /**
     * The mode of transport used for this shipment.
     * Must be one of the values defined in the TransportType enum.
     */
    @NotNull(message = "Transport type is required")
    @Schema(
        description = "Mode of transport",
        example = "DIESEL_TRUCK",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = {"DIESEL_TRUCK", "ELECTRIC_TRUCK", "TRAIN", "FLIGHT", "SHIP"}
    )
    private TransportType transportType;
}
