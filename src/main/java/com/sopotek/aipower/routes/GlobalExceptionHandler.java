package com.sopotek.aipower.routes;

import io.jsonwebtoken.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(@NotNull NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("The requested endpoint was not found: " + ex.getRequestURL());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(@NotNull Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(@NotNull IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body("Invalid request: " + ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(@NotNull IOException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
               .body("Service is temporarily unavailable due to an I/O error.");

    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleUnsupportedMediaTypeException(@NotNull HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
               .body("Unsupported media type: " + ex.getMessage());
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<?> handleTokenValidationException(@NotNull TokenValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
               .body("Invalid token: " + ex.getMessage());
    }
}
