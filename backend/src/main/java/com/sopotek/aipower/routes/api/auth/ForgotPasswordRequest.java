package com.sopotek.aipower.routes.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    private String email;
    public ForgotPasswordRequest() {

    }
}
