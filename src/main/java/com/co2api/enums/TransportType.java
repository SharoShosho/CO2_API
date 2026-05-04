package com.co2api.enums;

/**
 * Enum representing supported transport modes for CO2 emission calculation.
 *
 * Each constant holds an emission factor expressed in kg CO2 per ton-kilometre (kg CO2 / t·km)
 * and a human-readable description.
 * These values are industry standard approximations and can be updated as needed.
 *
 * To add a new transport type, simply add a new constant with the appropriate emission factor
 * and description.
 */
public enum TransportType {

    /** Standard diesel-powered road freight truck. */
    DIESEL_TRUCK(0.11, "Standard diesel-powered road freight truck"),

    /** Electric freight truck — significantly lower emissions than diesel. */
    ELECTRIC_TRUCK(0.03, "Electric freight truck — significantly lower emissions than diesel"),

    /** Freight train — one of the most efficient land transport options. */
    TRAIN(0.02, "Freight train — one of the most efficient land transport options"),

    /** Cargo aircraft — highest emission intensity per ton-km. */
    FLIGHT(0.50, "Cargo aircraft — highest emission intensity per ton-km"),

    /** Ocean cargo vessel — very efficient for large volumes over long distances. */
    SHIP(0.01, "Ocean cargo vessel — very efficient for large volumes over long distances");

    /**
     * The CO2 emission factor for this transport type,
     * expressed in kg CO2 per ton-kilometre (kg CO2 / t·km).
     */
    private final double emissionFactor;

    /** Human-readable description of this transport type. */
    private final String description;

    TransportType(double emissionFactor, String description) {
        this.emissionFactor = emissionFactor;
        this.description = description;
    }

    /**
     * Returns the emission factor (kg CO2 / t·km) for this transport type.
     *
     * @return emission factor as a double
     */
    public double getEmissionFactor() {
        return emissionFactor;
    }

    /**
     * Returns the human-readable description of this transport type.
     *
     * @return description string
     */
    public String getDescription() {
        return description;
    }
}
