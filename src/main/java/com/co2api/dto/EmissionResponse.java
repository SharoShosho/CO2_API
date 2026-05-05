package com.co2api.dto;

import com.co2api.enums.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for the CO2 emission calculation response.
 *
 * Returned by the POST /api/v1/calculate endpoint.
 * Uses the Builder pattern (via Lombok @Builder) for clean construction in the service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Response containing the calculated CO2 emission for a shipment.",
        example = "{\"transportType\":\"DIESEL_TRUCK\",\"weightKg\":5000.0,\"distanceKm\":800.0,\"totalCo2Kg\":440.0,\"emissionFactor\":0.11}"
)
public class EmissionResponse {

    /** Transport mode used in the calculation. */
    @Schema(description = "Transport mode used", example = "DIESEL_TRUCK")
    private TransportType transportType;

    /** Shipment weight in kilograms as provided in the request. */
    @Schema(description = "Weight of the shipment in kilograms", example = "5000.0")
    private double weightKg;

    /** Shipment distance in kilometres as provided in the request. */
    @Schema(description = "Distance of the shipment in kilometres", example = "800.0")
    private double distanceKm;

    /** Total calculated CO2 emission in kilograms. */
    @Schema(description = "Total CO2 emission in kilograms", example = "440.0")
    private double totalCo2Kg;

    /**
     * The emission factor (kg CO2 / t·km) used in this calculation.
     * Useful for transparency and debugging.
     */
    @Schema(description = "Emission factor applied (kg CO2 per ton-km)", example = "0.11")
    private double emissionFactor;
}
