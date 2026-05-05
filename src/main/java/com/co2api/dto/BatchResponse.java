package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body for the POST /api/v1/calculate/batch endpoint.
 *
 * Contains one EmissionResponse per shipment (in request order)
 * and the aggregated total CO2 across all shipments.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for a batch CO2 emission calculation")
public class BatchResponse {

    /** Individual emission results, one per shipment in request order. */
    @Schema(description = "Emission results for each shipment in the same order as the request")
    private List<EmissionResponse> results;

    /** Sum of totalCo2Kg across all shipments in the batch. */
    @Schema(description = "Total CO2 emission in kilograms for the entire batch", example = "447.2")
    private double totalCo2Kg;
}
