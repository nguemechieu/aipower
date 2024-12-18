package com.sopotek.aipower.routes.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@ControllerAdvice
public class CustomException {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(@NotNull HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "Method Not Allowed", "message", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class
            )
    public String handleAccessDeniedException( AccessDeniedException ex) {
         return "Access denied: " + ex.getMessage();
    }

    @ExceptionHandler(
            IllegalArgumentException.class

    )
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
         return "Invalid input: " + ex.getMessage();
    }

    @ExceptionHandler(
            NullPointerException.class
    )
    public String handleNullPointerException(NullPointerException ex) {
         return "Null pointer: " + ex.getMessage();
    }

    @ExceptionHandler(
            IllegalStateException.class
    )
    public String handleIllegalStateException( IllegalStateException ex) {
         return "Illegal state: " + ex.getMessage();
    }

    @ExceptionHandler(
            SecurityException.class
    )
    public String handleSecurityException(SecurityException ex) {
         return "Security exception: " + ex.getMessage();
    }

    @ExceptionHandler(
          HttpSessionRequiredException.class
    )
    public String handleException(HttpSessionRequiredException ex) {
         return "An unexpected error occurred: " + ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error", "message", ex.getMessage()));
    }





}
