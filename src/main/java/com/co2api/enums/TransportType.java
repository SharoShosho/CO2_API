package com.co2api.enums;

/**
 * Enum representing supported transport modes for CO2 emission calculation.
 *
 * Each constant holds an emission factor expressed in kg CO2 per ton-kilometre (kg CO2 / t·km).
 * These values are industry standard approximations and can be updated as needed.
 *
 * To add a new transport type, simply add a new constant with the appropriate emission factor.
 */
public enum TransportType {

    /** Standard diesel-powered road freight truck. */
    DIESEL_TRUCK(0.11),

    /** Electric freight truck — significantly lower emissions than diesel. */
    ELECTRIC_TRUCK(0.03),

    /** Freight train — one of the most efficient land transport options. */
    TRAIN(0.02),

    /** Cargo aircraft — highest emission intensity per ton-km. */
    FLIGHT(0.50),

    /** Ocean cargo vessel — very efficient for large volumes over long distances. */
    SHIP(0.01);

    /**
     * The CO2 emission factor for this transport type,
     * expressed in kg CO2 per ton-kilometre (kg CO2 / t·km).
     */
    private final double emissionFactor;

    TransportType(double emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

    /**
     * Returns the emission factor (kg CO2 / t·km) for this transport type.
     *
     * @return emission factor as a double
     */
    public double getEmissionFactor() {
        return emissionFactor;
    }
}
