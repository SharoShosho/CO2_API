package com.co2api.exception;

import com.co2api.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Global exception handler that converts validation, deserialization, and
 * unexpected errors into consistent {@link ApiErrorResponse} JSON responses.
 *
 * <ul>
 *   <li>400 Bad Request — Bean Validation failures or malformed JSON</li>
 *   <li>500 Internal Server Error — any other unhandled exception</li>
 * </ul>
 *
 * To handle a new exception type, add a new {@code @ExceptionHandler} method
 * following the same pattern used below.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures (@Valid, @NotNull, @Min, @Size, etc.)
     * triggered when a request body fails validation.
     *
     * @param ex      the validation exception containing field errors
     * @param request the current HTTP request (used to populate the path field)
     * @return 400 Bad Request with an {@link ApiErrorResponse}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> details = Stream.concat(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage),
                ex.getBindingResult().getGlobalErrors().stream()
                        .map(ObjectError::getDefaultMessage)
        ).collect(Collectors.toList());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed for the request body")
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles malformed JSON or unknown enum values in the request body.
     *
     * @param ex      the exception thrown when the message body cannot be read
     * @param request the current HTTP request
     * @return 400 Bad Request with an {@link ApiErrorResponse}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Malformed request body")
                .path(request.getRequestURI())
                .details(List.of(
                        "The request body is missing or contains invalid JSON. "
                        + "Check that all enum values are valid (e.g. DIESEL_TRUCK, TRAIN, FLIGHT, SHIP, ELECTRIC_TRUCK)."))
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Catch-all handler for any unexpected exception not covered by the
     * more specific handlers above. Returns HTTP 500 with a safe error message.
     *
     * @param ex      the unhandled exception
     * @param request the current HTTP request
     * @return 500 Internal Server Error with an {@link ApiErrorResponse}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .details(List.of("No additional details available"))
                .build();

        return ResponseEntity.internalServerError().body(body);
    }
}
