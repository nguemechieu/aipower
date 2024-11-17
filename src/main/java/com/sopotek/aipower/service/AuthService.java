package com.sopotek.aipower.service;



import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class AuthService {
@Value("${application.secret.key}")
private     String SECRET_KEY;
    int EXPIRATION_TIME_MINUTES;

    public AuthService() {



        this.EXPIRATION_TIME_MINUTES = 60 * 60 * 24 * 7;//Integer.parseInt(propertyResolver.getProperty("ai.power.security.token.expiration"));
    }


    public boolean validateToken(String token) {
        try {
            // Parse the token to verify it with the secret key
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))  // Use the secret key for verification
                    .build().parseClaimsJws(token);  // This will throw an exception if the token is invalid

            return true;
        } catch (Exception e) {
            // Token validation failed (either expired or invalid)
            return false;
        }
    }

    // Extract JWT token from cookies
    public String extractTokenFromCookies(@NotNull HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }




}