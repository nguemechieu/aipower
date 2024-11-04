package com.sopotek.aipower;


import com.sun.nio.sctp.IllegalUnbindException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Log LOG = LogFactory.getLog(GlobalExceptionHandler.class.getName());

    @Operation(summary = "Handle API exceptions and map to HTTP responses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "409", description = "Conflict"),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "511", description = "Network Authentication Required")
    })
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(@NotNull Exception ex) {
        // Handle specific exceptions and map to appropriate HTTP status codes
        if (ex instanceof IOException) {
            LOG.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service is temporarily unavailable due to an I/O error.");
        } else if (ex instanceof IllegalArgumentException) {
            LOG.error(
                    ex.getMessage(), ex
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: " + ex.getMessage());
        } else if (ex instanceof IllegalStateException) {
            LOG.error(
                    ex.getMessage(), ex
            );
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict: " + ex.getMessage());
        } else if (ex instanceof SecurityException) {
            LOG.error(
                    ex.getMessage(), ex
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: Access is denied.");
        }else if (
                Objects.requireNonNull(ex.getMessage(), "An error occurred while processing your request")
                       .contains("HTTP 415 Unsupported Media Type")
        ){
            LOG.error(
                    ex.getMessage(), ex
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("An error occurred while processing your request." +ex.getMessage());
        }
        // Log all other exceptions
        LOG.error(
                ex.getMessage(), ex
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("An unexpected error occurred. Please try again later.");
    }


}