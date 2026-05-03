package com.co2api.service;

import com.co2api.dto.EmissionResponse;
import com.co2api.dto.ShipmentRequest;
import org.springframework.stereotype.Service;

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
}
