package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response payload returned by the {@code POST /api/v1/compare/best} endpoint.
 *
 * Contains the single best (lowest-emission) result and a ranked list of
 * all supported transport types so callers can see the full picture.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Best-route result showing lowest-emission transport type and a full ranked list.")
public class BestRouteResponse {

    @Schema(description = "The transport type with the lowest CO2 emission for the given shipment")
    private EmissionResponse best;

    @Schema(description = "All supported transport types ranked from lowest to highest CO2 emission")
    private List<EmissionResponse> ranked;
}
