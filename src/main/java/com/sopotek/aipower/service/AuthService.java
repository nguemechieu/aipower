package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class AuthService {

    private static final Log LOG = LogFactory.getLog(AuthService.class);
    @Value("${aipower.jwt.secret.key}")
    private   String secretKey ;

    private  SecretKey key;

    @Value("${aipower.jwt.token-expiration-time}")
    private int accessTokenExpirationMinutes=1440;

    @Value("${aipower.jwt.refresh-token-expiration-time}")
    private int refreshTokenExpirationMinutes;
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();
    private final RoleService roleService;

    @Autowired
    public AuthService(RoleService roleService) {
        this.roleService = roleService;
        this.secretKey=   Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        this. key=Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtAccessToken(String username, @NotNull Collection<? extends GrantedAuthority> authorities) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) accessTokenExpirationMinutes * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtRefreshToken(@NotNull User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) refreshTokenExpirationMinutes * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                LOG.warn("Token is blacklisted");
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            LOG.error("JWT validation failed: " + e.getMessage(), e);
            return false;
        }
    }
//
//    public String extractTokenFromCookies(@NotNull HttpServletRequest request) {
//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if ("jwt".equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        LOG.warn("JWT cookie not found");
//        return null;
//    }

    public String getUserFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            LOG.error("Failed to extract user from JWT: " + e.getMessage(), e);
            return null;
        }
    }

    public List<GrantedAuthority> getRolesFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            List<?> roles = claims.get("roles", List.class);
            return roles == null ? List.of() :
                    roles.stream()
                            .filter(role -> role instanceof String)
                            .map(role -> new SimpleGrantedAuthority((String) role))
                            .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to extract roles from JWT: " + e.getMessage(), e);
            return List.of();
        }
    }

//    public void blacklistToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        Date expiration = claims.getExpiration();
//        if (expiration != null) {
//            tokenBlacklist.put(token, expiration.getTime());
//            LOG.info("Token blacklisted: " + token);
//        }
//    }

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

    public String validateJwtRefreshToken(String refreshToken) {
        try {
            if (isTokenBlacklisted(refreshToken)) {
                LOG.warn("Refresh token is blacklisted");
                return null;
            }
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // Ensure the token is not expired
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                LOG.warn("Refresh token is expired");
                return null;
            }

            // Return the username from the token's subject
            return claims.getSubject();
        } catch (Exception e) {
            LOG.error("Failed to validate refresh token: " + e.getMessage(), e);
            return null;
        }
    }

    public String generateJwtRefreshToken(String username, @NotNull List<GrantedAuthority> authorities) {
        return Jwts.builder()
               .setSubject(username)
               .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + (long) refreshTokenExpirationMinutes * 60 * 1000))
               .signWith(key, SignatureAlgorithm.HS256)
               .compact();
    }
}
