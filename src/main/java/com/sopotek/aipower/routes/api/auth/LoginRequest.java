package com.sopotek.aipower.routes.api.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private Date timestamp;

    public LoginRequest() {
    }

// Constructors, getters, setters, etc.
}
