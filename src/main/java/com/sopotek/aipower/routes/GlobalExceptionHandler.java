package com.sopotek.aipower.routes;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global Exception Handler for handling exceptions application-wide.
 * This class provides a centralized mechanism to handle and format exceptions consistently,
 * returning meaningful error responses to the client while logging them for developers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Builds a standardized error response for all exceptions.
     * Logs the error details for debugging and monitoring purposes.
     *
     * @param status  The HTTP status code to be returned.
     * @param message A user-friendly error message for the client.
     * @param ex      The exception being handled, for logging purposes.
     * @return A ResponseEntity containing a structured error response.
     */
    private @NotNull ResponseEntity<ResponseError> buildErrorResponse(HttpStatus status, String message, Throwable ex) {
        // Log the exception details at ERROR level for visibility in logs
        log.error("Error: {}, Status: {}, Message: {}", ex.getClass().getSimpleName(), status, ex.getMessage(), ex);

        // Construct the error response object
        ResponseError response = new ResponseError(
                status.value(),                // Numeric HTTP status code
                status.getReasonPhrase(),      // Textual representation of the HTTP status (e.g., "Bad Request")
                message,                       // Custom message describing the error
                LocalDateTime.now()            // Timestamp for when the error occurred
        );

        // Return the response wrapped in a ResponseEntity with the given HTTP status
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles validation errors thrown during method argument validation.
     * Captures all field-level errors and returns them in a structured format.
     *
     * @param ex The MethodArgumentNotValidException thrown by Spring during validation.
     * @return A ResponseEntity containing validation error details and a BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(@NotNull MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract field-specific validation errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage()); // Field name and error message
        }

        String message = "Validation failed for one or more fields.";
        // Return structured validation errors along with a BAD_REQUEST status
        return
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Map.of(
                                "errors", errors,
                                "message", message
                        )
                );
    }

    /**
     * Handles cases where a requested resource does not exist.
     * Typically thrown when querying for non-existent database records.
     *
     * @param ex The NoSuchElementException indicating a missing resource.
     * @return A ResponseEntity with a NOT_FOUND status and error message.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(@NotNull NoSuchElementException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource not found: " + ex.getMessage(), ex);
    }

    /**
     * Catches and handles all unexpected exceptions that are not explicitly mapped.
     * Ensures the application does not crash and provides a generic error response.
     *
     * @param ex The generic Exception caught by the handler.
     * @return A ResponseEntity with an INTERNAL_SERVER_ERROR status and error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(@NotNull Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."+ ex.getMessage(), ex);
    }

    /**
     * Handles cases where the client requests a non-existent endpoint.
     * Commonly occurs when the requested URL does not match any route.
     *
     * @param ex The NoHandlerFoundException indicating an unmatched URL.
     * @return A ResponseEntity with a NOT_FOUND status and error message.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(@NotNull NoHandlerFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "The requested endpoint was not found: " + ex.getRequestURL(), ex);
    }

    /**
     * Handles cases where invalid arguments are passed to a method.
     * Often thrown when a method receives an inappropriate or illegal argument.
     *
     * @param ex The IllegalArgumentException thrown by the application.
     * @return A ResponseEntity with a BAD_REQUEST status and error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(@NotNull IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request: " + ex.getMessage(), ex);
    }

    /**
     * Handles I/O-related exceptions, such as network or file access errors.
     * Provides a SERVICE_UNAVAILABLE status to indicate a temporary issue.
     *
     * @param ex The IOException indicating an input/output error.
     * @return A ResponseEntity with a SERVICE_UNAVAILABLE status and error message.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(@NotNull IOException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service is temporarily unavailable due to an I/O error.", ex);
    }

    /**
     * Handles cases where the client sends a request with an unsupported media type.
     * Typically, occurs when the `Content-Type` header does not match any accepted format.
     *
     * @param ex The HttpMediaTypeNotSupportedException indicating the issue.
     * @return A ResponseEntity with an UNSUPPORTED_MEDIA_TYPE status and error message.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleUnsupportedMediaTypeException(@NotNull HttpMediaTypeNotSupportedException ex) {
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type: " + ex.getMessage(), ex);
    }

    /**
     * Handles custom exceptions related to token validation failures.
     * This ensures unauthorized access attempts are logged and appropriately handled.
     *
     * @param ex The TokenValidationException thrown during token validation.
     * @return A ResponseEntity with an UNAUTHORIZED status and error message.
     */
    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<?> handleTokenValidationException(@NotNull TokenValidationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token: " + ex.getMessage(), ex);
    }



     @ExceptionHandler(TokenExpiredException.class)
     public ResponseEntity<?> handleTokenExpiredException(@NotNull TokenExpiredException ex) {
         return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token expired: " + ex.getMessage(), ex);
     }
    /**
     * Standardized error response format for all exceptions.
     * Contains details such as HTTP status, error type, custom message, and timestamp.
     */
    public record ResponseError(
            int status,           // HTTP status code (e.g., 400, 404, 500)
            String error,         // Error type (e.g., "Bad Request", "Not Found")
            String message,       // Custom message describing the error
            LocalDateTime timestamp // Timestamp of when the error occurred
    ) {}
}
