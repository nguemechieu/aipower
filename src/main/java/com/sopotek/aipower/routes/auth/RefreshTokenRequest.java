package com.sopotek.aipower.routes.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    private String refreshToken;
    private String accessToken;
    private String id;
    private String id2;
    public RefreshTokenRequest(String refreshToken, String accessToken, String id, String id2) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.id = id;
        this.id2 = id2;
    }
}
