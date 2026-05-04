package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request body for the POST /api/v1/calculate/batch endpoint.
 *
 * Wraps a list of ShipmentRequests to be calculated in a single call.
 * A maximum of 100 shipments per batch is enforced to prevent abuse.
 */
@Data
@Schema(description = "Request payload for batch CO2 emission calculation")
public class BatchRequest {

    /** Maximum allowed number of shipments per batch request. */
    public static final int MAX_BATCH_SIZE = 100;

    /**
     * List of shipment requests to calculate.
     * Must not be null or empty, and cannot exceed MAX_BATCH_SIZE entries.
     */
    @NotNull(message = "shipments list is required")
    @NotEmpty(message = "shipments list must not be empty")
    @Size(max = MAX_BATCH_SIZE, message = "Batch size must not exceed " + MAX_BATCH_SIZE + " shipments")
    @Valid
    @Schema(description = "List of shipment requests (max " + MAX_BATCH_SIZE + ")",
            required = true)
    private List<ShipmentRequest> shipments;
}
