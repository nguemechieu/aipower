package com.sopotek.aipower.routes.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {
    }

    private boolean rememberMe;
}