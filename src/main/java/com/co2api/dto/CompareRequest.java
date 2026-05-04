package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for the POST /api/v1/compare endpoint.
 *
 * Contains two shipment options (A and B) to compare CO2 emissions side by side.
 */
@Data
@Schema(description = "Request payload for comparing CO2 emissions of two shipment options")
public class CompareRequest {

    /** First shipment option (A). */
    @NotNull(message = "optionA is required")
    @Valid
    @Schema(description = "First shipment option to compare", required = true)
    private ShipmentRequest optionA;

    /** Second shipment option (B). */
    @NotNull(message = "optionB is required")
    @Valid
    @Schema(description = "Second shipment option to compare", required = true)
    private ShipmentRequest optionB;
}
