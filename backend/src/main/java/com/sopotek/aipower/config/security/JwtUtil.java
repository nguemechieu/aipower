package com.sopotek.aipower.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Getter
@Setter
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    public JwtUtil() {
        logger.atDebug().log("JwtUtil created");
        logger.info("JwtUtil created");
    }

    @Value("${aipower.jwt.secret.key}")
    private  String secretKey;

    @Contract(" -> new")
    private  @NotNull SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Value("${aipower.jwt.token-expiration-time}")
    private  long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    // Generate a token
    public  String generateToken(String username) {
        return Jwts.builder().subject(username).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey()) // Automatically determines the algorithm from the key
                .compact();
    }

    // Validate a token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())

                    .build()
                    .parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // Extract username from token
    public String extractUsername(String token) {
        return Jwts.parser().decryptWith(getSigningKey())
                .build()
                .parse(token).toString();
    }

    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }


    public String generateAccessToken(@NotNull UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String generateRefreshToken(@NotNull UserDetails userDetails) {
        return Jwts.builder().subject(userDetails.getUsername()).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7))) // 7 days
               .signWith(getSigningKey())
               .compact();
    }
}
