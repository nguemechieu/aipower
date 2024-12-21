package com.sopotek.aipower.routes.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    private String accessToken;
    private String role;
    private String username;
    public RefreshTokenRequest(String accessToken, String username, String role) {

        this.accessToken = accessToken;
        this.username = username;
        this.role= role;
    }
}
