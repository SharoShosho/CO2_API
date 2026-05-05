package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body used by the compare endpoint.
 */
@Data
@Schema(description = "Request payload for comparing two shipment emission calculations")
public class CompareRequest {

    @NotNull(message = "Option A is required")
    @Valid
    @Schema(description = "First shipment option to compare")
    private ShipmentRequest optionA;

    @NotNull(message = "Option B is required")
    @Valid
    @Schema(description = "Second shipment option to compare")
    private ShipmentRequest optionB;
}

