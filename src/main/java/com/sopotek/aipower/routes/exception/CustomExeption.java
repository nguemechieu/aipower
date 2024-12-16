package com.sopotek.telegramtrader.routes.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class CustomExeption {

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
