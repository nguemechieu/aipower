package com.sopotek.aipower.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Getter
@Setter

@Service
public class JwtService {



    private long accessTokenExpiration;


    // Validate the token and issue a new access token
    public String refreshAccessToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token.");
        }

        Claims claims = extractAllClaims(refreshToken);

        // Extract the subject (e.g., username) and roles to generate a new access token
        String username = claims.getSubject();

        return generateToken(username);
    }

    @Value("${aipower.jwt.refreshExpiration}")
    private long refreshTokenExpiration;
    @Value("${aipower.jwt.secret.key}")
    private String jwtSecret;
    @Value("${aipower.jwt.token-expiration-time}")
    private static final int JWT_EXPIRATION_MS = 3600 * 1000; // 1 hour in milliseconds
    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username
     * @return a JWT token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        // Generate a secure key from the secret
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512) // Use the secure key
                .compact();
    }

    // Validate token
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {
        byte[] key;
        try {
            key = jwtSecret.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the JWT secret key.");
        }
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        // Generate a secure key from the secret
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512) // Use the secure key
                .compact();
    }
}
