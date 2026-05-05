package com.co2api.service;

import com.co2api.dto.BatchRequest;
import com.co2api.dto.BatchResponse;
import com.co2api.dto.CompareRequest;
import com.co2api.dto.CompareResponse;
import com.co2api.dto.EmissionResponse;
import com.co2api.dto.ShipmentRequest;
import com.co2api.dto.TransportTypeResponse;
import com.co2api.enums.TransportType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating CO2 emissions based on shipment data.
 *
 * The calculation uses the following formula:
 *   CO2 (kg) = weight (kg) / 1000 * distance (km) * emissionFactor (kg CO2 / t·km)
 *
 * Emission factors are defined on the TransportType enum and represent
 * industry-standard approximate values (kg CO2 per ton-kilometre).
 *
 * This class is annotated with @Service so that Spring registers it as a
 * managed bean that can be injected into controllers or other services.
 */
@Service
public class EmissionService {

    /**
     * Calculates the total CO2 emission for a given shipment request.
     *
     * Steps:
     *  1. Convert weight from kilograms to metric tons (1 ton = 1000 kg).
     *  2. Multiply weight (tons) × distance (km) × emission factor (kg CO2 / t·km).
     *  3. Build and return an EmissionResponse with the result and metadata.
     *
     * @param request the shipment data (weight, distance, transport type)
     * @return an EmissionResponse containing the total CO2 in kg and calculation details
     */
    public EmissionResponse calculateEmissions(ShipmentRequest request) {

        // Retrieve the emission factor from the chosen transport type
        double emissionFactor = request.getTransportType().getEmissionFactor();

        // Convert weight from kg to metric tons for the ton-km formula
        double weightTons = request.getWeightKg() / 1000.0;

        // Core calculation: CO2 (kg) = weight (t) × distance (km) × factor (kg CO2 / t·km)
        double totalCo2 = weightTons * request.getDistanceKm() * emissionFactor;

        // Build and return the response DTO using Lombok's builder pattern
        return EmissionResponse.builder()
                .transportType(request.getTransportType())
                .weightKg(request.getWeightKg())
                .distanceKm(request.getDistanceKm())
                .emissionFactor(emissionFactor)
                .totalCo2Kg(totalCo2)
                .build();
    }

    /**
     * Returns metadata for all supported transport types derived from the TransportType enum.
     *
     * @return list of TransportTypeResponse, one per enum constant
     */
    public List<TransportTypeResponse> getAllTransportTypes() {
        return Arrays.stream(TransportType.values())
                .map(t -> TransportTypeResponse.builder()
                        .code(t.name())
                        .emissionFactor(t.getEmissionFactor())
                        .unit("kg CO2 / t·km")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Calculates CO2 emissions for a batch of shipment requests.
     *
     * Each shipment is calculated individually and the results are aggregated.
     *
     * @param batchRequest the batch containing a list of ShipmentRequests
     * @return a BatchResponse with individual results and total CO2 across all shipments
     */
    public BatchResponse calculateBatch(BatchRequest batchRequest) {
        List<EmissionResponse> results = batchRequest.getShipments().stream()
                .map(this::calculateEmissions)
                .collect(Collectors.toList());

        double totalCo2Kg = results.stream()
                .mapToDouble(EmissionResponse::getTotalCo2Kg)
                .sum();

        return BatchResponse.builder()
                .results(results)
                .totalCo2Kg(totalCo2Kg)
                .build();
    }

    /**
     * Compares CO2 emissions for two shipment options (A and B).
     *
     * Calculates the absolute and relative difference, and determines which option
     * produces fewer CO2 emissions.
     *
     * differenceKg      = optionA.totalCo2Kg − optionB.totalCo2Kg
     * differencePercent = (A − B) / A × 100  (0.0 when A == 0 to avoid division by zero)
     * betterOption      = "OPTION_A" | "OPTION_B" | "EQUAL"
     *
     * @param compareRequest the compare request containing optionA and optionB
     * @return a CompareResponse with side-by-side results and comparison metrics
     */
    public CompareResponse compareEmissions(CompareRequest compareRequest) {
        EmissionResponse resultA = calculateEmissions(compareRequest.getOptionA());
        EmissionResponse resultB = calculateEmissions(compareRequest.getOptionB());

        double co2A = resultA.getTotalCo2Kg();
        double co2B = resultB.getTotalCo2Kg();

        double differenceKg = co2A - co2B;

        // Avoid division by zero: if A emits nothing (or effectively zero), percentage difference is 0
        double differencePercent = (Math.abs(co2A) < 1e-10) ? 0.0 : (differenceKg / co2A) * 100.0;

        String betterOption;
        if (co2A < co2B) {
            betterOption = "OPTION_A";
        } else if (co2B < co2A) {
            betterOption = "OPTION_B";
        } else {
            betterOption = "EQUAL";
        }

        double absoluteDifferenceKg = Math.abs(differenceKg);

        String summary;
        if ("OPTION_A".equals(betterOption)) {
            summary = "Option A has " + absoluteDifferenceKg + " kg lower CO2 than option B.";
        } else if ("OPTION_B".equals(betterOption)) {
            summary = "Option B has " + absoluteDifferenceKg + " kg lower CO2 than option A.";
        } else {
            summary = "Both options have the same CO2 emissions.";
        }

        return CompareResponse.builder()
                .optionA(resultA)
                .optionB(resultB)
                .differenceCo2Kg(absoluteDifferenceKg)
                .lowerEmissionOption(betterOption)
                .summary(summary)
                .build();
    }
}
