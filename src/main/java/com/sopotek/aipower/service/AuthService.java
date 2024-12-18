package com.sopotek.aipower.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Getter
@Setter

@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private static final String ROLES_CLAIM = "role";

    private SecretKey key;
    private int accessTokenExpirationMinutes;
    private  int refreshTokenExpirationMinutes;
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();
    private  JwtParser jwtParser;


    @Autowired
    public AuthService(
            @Value("${aipower.jwt.secret.key}") String secretKey,
            @Value("${aipower.jwt.token-expiration-time}")
            int accessTokenExpirationMinutes,
            @Value("${aipower.jwt.refresh-token-expiration-time}") int refreshTokenExpirationMinutes) {

        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
        this.jwtParser = Jwts.parser().build()
                ;
    }


    /**
     * Generates a JWT access token.
     *
     * @param username    The username for the token subject.
     * @param authorities The user's granted authorities (roles).
     * @return The signed JWT access token.
     */
    public String generateJwtAccessToken(String username, @NotNull Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder().subject(username)
                .claim(ROLES_CLAIM, authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())).issuedAt(new Date()).expiration(calculateExpirationDate(accessTokenExpirationMinutes))
                .signWith(key)
                .compact();
    }


    public boolean validateJwtToken(String token) {
        if (isTokenBlacklisted(token)) {
            LOG.warn("Token is blacklisted");
            return false;
        }

        try {
            jwtParser.parse(token);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to validate JWT: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token.
     * @return The username or null if extraction fails.
     */
    public String getUserFromJwtToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            LOG.error("Failed to extract user from JWT: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extracts roles from a JWT token.
     *
     * @param token The JWT token.
     * @return A list of GrantedAuthority objects or an empty list if extraction fails.
     */
    public List<GrantedAuthority> getRolesFromJwtToken(String token) {
        try {
            Claims claims = parseClaims(token);
            List<?> roles = claims.get(ROLES_CLAIM, List.class);
            return roles == null ? List.of() :
                    roles.stream()
                            .filter(role -> role instanceof String)
                            .map(role -> new SimpleGrantedAuthority((String) role))
                            .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to extract roles from JWT: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Checks if a token is blacklisted.
     *
     * @param token The token to check.
     * @return True if the token is blacklisted, false otherwise.
     */
    public boolean isTokenBlacklisted(String token) {
        Long expirationTime = tokenBlacklist.get(token);
        if (expirationTime == null) {
            return false;
        }
        if (expirationTime < System.currentTimeMillis()) {
            tokenBlacklist.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Parses claims from a JWT token.
     *
     * @param token The JWT token.
     * @return The Claims object.
     */
    private Claims parseClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    /**
     * Calculates the expiration date based on the current time and given minutes.
     *
     * @param minutes The number of minutes until expiration.
     * @return The calculated expiration date.
     */
    private Date calculateExpirationDate(int minutes) {
        return new Date(System.currentTimeMillis() + (long) minutes * 60 * 1000);
    }

    public String validateJwtRefreshToken(String refreshToken) {
        try {
            Claims claims = jwtParser.parseSignedClaims(refreshToken).getPayload();
            String username = claims.getSubject();
            if (username == null || !validateJwtToken(generateJwtAccessToken(username, getRolesFromJwtToken(refreshToken)))) {
                return null;
            }
            return username;
        } catch (Exception e) {
            LOG.error("Failed to validate refresh token: {}", e.getMessage(), e);
            return null;
        }
    }



        private final RestTemplate restTemplate = new RestTemplate();

        public String exchangeGoogleCodeForAccessToken(String code) {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            String redirectUri = "http://localhost:3000/api/v3/auth/google/callback";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "code", code,
                    "client_id", "539426084783-ju5ppl2ofi85nk1bti3ic6hos6vrr62s.apps.googleusercontent.com",
                    "client_secret", "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",
                    "redirect_uri", redirectUri,
                    "grant_type", "authorization_code"
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<?> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to exchange Google code for access token: " + response.getStatusCode());
            }
            Map<String, Object> responses = (Map<String, Object>) response.getBody();

            assert responses != null;
            return (String) responses.get("access_token");
        }

        public ResponseEntity<?> fetchGoogleUserInfo(String accessToken) {
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, Map.class);
        }




}
