package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body returned by the compare endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comparison result between two shipment emission calculations")
public class CompareResponse {

    @Schema(description = "Calculated emission for option A")
    private EmissionResponse optionA;

    @Schema(description = "Calculated emission for option B")
    private EmissionResponse optionB;

    @Schema(description = "Absolute difference in CO2 emissions between option A and B", example = "120.5")
    private double differenceCo2Kg;

    @Schema(description = "Which option has the lower emission", example = "OPTION_A")
    private String lowerEmissionOption;

    @Schema(description = "Short human-readable explanation of the result", example = "Option A has 120.5 kg lower CO2 than option B.")
    private String summary;
}

