package com.co2api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error response returned by the API for validation, authentication,
 * and unexpected server errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response returned by the API")
public class ApiErrorResponse {

    @Schema(
            description = "Timestamp when the error occurred",
            example = "2026-05-06T12:34:56.789Z"
    )
    private OffsetDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Short error name", example = "Bad Request")
    private String error;

    @Schema(description = "Human-readable error message", example = "Validation failed for the request body")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/v1/calculate")
    private String path;

    @Schema(
            description = "Optional list of detailed validation or processing issues",
            example = "[\"weightKg must be greater than or equal to 1\", \"transportType is required\"]"
    )
    private List<String> details;
}

