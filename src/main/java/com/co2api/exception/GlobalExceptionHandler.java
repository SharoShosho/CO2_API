package com.co2api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler that converts validation and deserialization errors
 * into consistent, client-friendly JSON responses.
 *
 * Returns HTTP 400 Bad Request with a structured body containing a list of
 * field-level or top-level error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures (@Valid, @NotNull, @Min, @Size, etc.)
     * triggered when a request body fails validation.
     *
     * Response example:
     * {
     *   "status": 400,
     *   "error": "Validation failed",
     *   "messages": ["weightKg: Weight is required", "transportType: Transport type is required"]
     * }
     *
     * @param ex the validation exception containing field errors
     * @return 400 Bad Request with a map of error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        // Include object-level constraint violations (e.g. @NotEmpty on a list field)
        ex.getBindingResult().getGlobalErrors().stream()
                .map(e -> e.getDefaultMessage())
                .forEach(messages::add);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("messages", messages);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles malformed JSON or unknown enum values in the request body.
     *
     * @param ex the exception thrown when the message body cannot be read
     * @return 400 Bad Request with a descriptive error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Malformed request body");
        body.put("messages", List.of("The request body is missing or contains invalid JSON. " +
                "Check that all enum values are valid (e.g. DIESEL_TRUCK, TRAIN, FLIGHT, SHIP, ELECTRIC_TRUCK)."));

        return ResponseEntity.badRequest().body(body);
    }
}
