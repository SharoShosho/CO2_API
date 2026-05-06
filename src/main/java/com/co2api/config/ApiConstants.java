package com.co2api.config;

/**
 * Central API constants shared across controllers and configuration.
 *
 * Using a single place for the version prefix ensures that all endpoints
 * move together when the API is versioned. To introduce v2, create a new
 * set of controllers that reference {@code ApiConstants.V2}.
 */
public final class ApiConstants {

    /** Base path prefix for version 1 of the API. */
    public static final String V1 = "/api/v1";

    /** Current API version string used in documentation and metadata. */
    public static final String VERSION = "1.0.4";

    private ApiConstants() {
        // Utility class – not instantiable
    }
}
