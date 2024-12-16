package com.sopotek.aipower.routes.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@ControllerAdvice
public class CustomExeption {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(@NotNull HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "Method Not Allowed", "message", ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error"; // Ensure "error.html" exists
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





}
