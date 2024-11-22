package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@Service
public class AuthService {

    private static final Log LOG = LogFactory.getLog(AuthService.class);

    @Value("${aipower.jwt.secret.key}")
    private String secretKey="${aipower.jwt.secret.key:345t6y7uwieop3rty}";

    @Value("${aipower.jwt.token-expiration-time}")
    private int expirationTimeMinutes=3600;
private RoleService roleService;
    protected SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
@Autowired
    public AuthService(RoleService roleService) {
        // Secret key initialization

        this.roleService=roleService;
    }

    public String generateJwtAccessToken(String username, @NotNull List<SimpleGrantedAuthority> authorities) {
        // Generate a new JWT token with the username and roles
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) expirationTimeMinutes * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            // Parse the token to verify it with the secret key
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // This will throw an exception if the token is invalid
            return true;
        } catch (Exception e) {
            // Token validation failed (either expired or invalid)
            LOG.error("JWT validation failed: " + e.getMessage(), e);
            return false;
        }
    }

    public String extractTokenFromCookies(@NotNull HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String getUserFromJwtToken(String token) {
        try {
            // Parse the token to get the username
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            // Token parsing failed (either expired or invalid)
            LOG.error("Failed to extract user from JWT: " + e.getMessage(), e);
            return null;
        }
    }

    public List<SimpleGrantedAuthority> getRolesFromJwtToken(String token) {
        try {
            // Parse the token to get the roles
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            List<String> roles = claims.get("roles", List.class);
            return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        } catch (Exception e) {
            // Token parsing failed (either expired or invalid)
            LOG.error("Failed to extract roles from JWT: " + e.getMessage(), e);
            return null;
        }
    }

    public String generateJwtRefreshToken(User user) {
        // Generate a refresh token (you can customize claims as needed)
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) expirationTimeMinutes * 2 * 60 * 1000)) // Example: double the access token expiry
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtAccessToken(@NotNull User user) {
        // Generate an access token with the username, roles, and refresh token
        return generateJwtAccessToken(user.getUsername(), user.getAuthorities().stream().map(
                role ->
                        new SimpleGrantedAuthority(role.getAuthority())
                ).collect(Collectors.toList()));




    }
}
