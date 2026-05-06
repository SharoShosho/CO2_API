package com.co2api.service;

import com.co2api.dto.BatchRequest;
import com.co2api.dto.BatchResponse;
import com.co2api.dto.BestRouteRequest;
import com.co2api.dto.BestRouteResponse;
import com.co2api.dto.CompareRequest;
import com.co2api.dto.CompareResponse;
import com.co2api.dto.EmissionResponse;
import com.co2api.dto.EstimateRequest;
import com.co2api.dto.EstimateResponse;
import com.co2api.dto.ShipmentRequest;
import com.co2api.dto.TransportTypeResponse;
import com.co2api.enums.TransportType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for all CO2 emission calculations.
 *
 * <p>The core formula used throughout this class:
 * <pre>CO2 (kg) = weight (kg) / 1000 × distance (km) × emissionFactor (kg CO2 / t·km)</pre>
 *
 * <p>Emission factors are defined on the {@link TransportType} enum and represent
 * industry-standard approximate values. To add a new transport type simply add a
 * new constant to the enum — no changes needed here.
 */
@Service
public class EmissionService {

    private static final String UNIT = "kg CO2 / t·km";

    /**
     * Calculates the total CO2 emission for a given shipment request.
     *
     * @param request the shipment data (weight, distance, transport type)
     * @return an EmissionResponse containing the total CO2 in kg and calculation details
     */
    public EmissionResponse calculateEmissions(ShipmentRequest request) {
        double emissionFactor = request.getTransportType().getEmissionFactor();
        double weightTons = request.getWeightKg() / 1000.0;
        double totalCo2 = weightTons * request.getDistanceKm() * emissionFactor;

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
                .map(this::toTransportTypeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns metadata for a single transport type looked up by its code string.
     *
     * @param code case-insensitive transport type code, e.g. "TRAIN"
     * @return the matching TransportTypeResponse
     * @throws ResponseStatusException 400 if the code does not match any TransportType
     */
    public TransportTypeResponse getTransportType(String code) {
        try {
            TransportType type = TransportType.valueOf(code.toUpperCase());
            return toTransportTypeResponse(type);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown transport type: '" + code + "'. Valid values are: "
                    + Arrays.stream(TransportType.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
        }
    }

    /**
     * Calculates CO2 emissions for a batch of shipment requests.
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
     * @param compareRequest the compare request containing optionA and optionB
     * @return a CompareResponse with side-by-side results and comparison metrics
     */
    public CompareResponse compareEmissions(CompareRequest compareRequest) {
        EmissionResponse resultA = calculateEmissions(compareRequest.getOptionA());
        EmissionResponse resultB = calculateEmissions(compareRequest.getOptionB());

        double co2A = resultA.getTotalCo2Kg();
        double co2B = resultB.getTotalCo2Kg();

        double differenceKg = co2A - co2B;
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

    /**
     * Finds the best (lowest-emission) transport type for a given shipment and
     * returns all options ranked from lowest to highest CO2.
     *
     * @param bestRouteRequest shipment weight and distance
     * @return BestRouteResponse with the best option and a full ranked list
     */
    public BestRouteResponse findBestRoute(BestRouteRequest bestRouteRequest) {
        List<EmissionResponse> ranked = Arrays.stream(TransportType.values())
                .map(type -> {
                    ShipmentRequest req = new ShipmentRequest();
                    req.setWeightKg(bestRouteRequest.getWeightKg());
                    req.setDistanceKm(bestRouteRequest.getDistanceKm());
                    req.setTransportType(type);
                    return calculateEmissions(req);
                })
                .sorted(Comparator.comparingDouble(EmissionResponse::getTotalCo2Kg))
                .collect(Collectors.toList());

        if (ranked.isEmpty()) {
            throw new IllegalStateException("No transport types available for best-route calculation");
        }

        return BestRouteResponse.builder()
                .best(ranked.get(0))
                .ranked(ranked)
                .build();
    }

    /**
     * Calculates CO2 using a caller-supplied emission factor instead of a
     * predefined {@link TransportType} value.
     *
     * @param estimateRequest weight, distance, and custom factor
     * @return EstimateResponse with the calculated CO2 total
     */
    public EstimateResponse estimateCustom(EstimateRequest estimateRequest) {
        double weightTons = estimateRequest.getWeightKg() / 1000.0;
        double totalCo2 = weightTons * estimateRequest.getDistanceKm() * estimateRequest.getCustomFactorKgPerTonKm();

        return EstimateResponse.builder()
                .weightKg(estimateRequest.getWeightKg())
                .distanceKm(estimateRequest.getDistanceKm())
                .customFactorKgPerTonKm(estimateRequest.getCustomFactorKgPerTonKm())
                .totalCo2Kg(totalCo2)
                .build();
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private TransportTypeResponse toTransportTypeResponse(TransportType type) {
        return TransportTypeResponse.builder()
                .code(type.name())
                .emissionFactor(type.getEmissionFactor())
                .unit(UNIT)
                .description(type.getDescription())
                .build();
    }
}
