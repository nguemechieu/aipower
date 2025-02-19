package com.sopotek.aipower.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter

@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

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
            JsonNode responses;
            try {
                responses = new ObjectMapper().readValue( response.toString(), JsonNode.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            LOG.info("Exchange {}", response);


            return  responses.get("access_token").toString();
        }

        public ResponseEntity<?> fetchGoogleUserInfo(String accessToken) {
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, Map.class);
        }




}
