package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body for the POST /api/v1/compare endpoint.
 *
 * Provides a side-by-side comparison of CO2 emissions for two shipment options
 * and indicates which option produces fewer emissions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of comparing CO2 emissions for two shipment options")
public class CompareResponse {

    /** Emission result for option A. */
    @Schema(description = "CO2 emission result for option A")
    private EmissionResponse optionA;

    /** Emission result for option B. */
    @Schema(description = "CO2 emission result for option B")
    private EmissionResponse optionB;

    /**
     * Absolute difference in CO2 emissions: optionA.totalCo2Kg − optionB.totalCo2Kg.
     * Positive value means A emits more; negative value means B emits more.
     */
    @Schema(description = "Difference in CO2 emissions (optionA - optionB) in kg", example = "360.0")
    private double differenceKg;

    /**
     * Relative difference as a percentage of option A's emissions.
     * Calculated as: (A − B) / A × 100.
     * Returns 0.0 when option A emits 0 kg CO2 to avoid division by zero.
     */
    @Schema(description = "Percentage difference relative to option A ((A-B)/A*100)", example = "81.82")
    private double differencePercent;

    /**
     * Indicates which option produces lower CO2 emissions.
     * Possible values: "OPTION_A", "OPTION_B", or "EQUAL".
     */
    @Schema(description = "The option with lower CO2 emissions", example = "OPTION_B",
            allowableValues = {"OPTION_A", "OPTION_B", "EQUAL"})
    private String betterOption;
}
