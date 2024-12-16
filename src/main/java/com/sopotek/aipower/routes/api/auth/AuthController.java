package com.sopotek.aipower.routes.api.auth;

import com.sopotek.aipower.model.Role;
import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST controller for authentication and user account management.
 * Provides endpoints for login, registration, and password reset functionalities.
 * Handles user-related requests while ensuring proper validation, logging, and error handling.
 */
@Getter
@Setter
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final LocalizationService localizationService;
    private final IPBlockService ipBlockService;
    private final AuthenticationManager authenticationManager;

    private String ipAddress;
    private String refreshToken;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, LocalizationService localizationService, IPBlockService ipBlockService,
                          UserService userService, AuthService authService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authService = authService;
        this.ipBlockService = ipBlockService;
        this.localizationService = localizationService;
        this.authenticationManager = authenticationManager;

        logger.info("AuthController initialized successfully");
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String clientIP = ipBlockService.getClientIP(request);

        if (ipBlockService.isBlocked(clientIP)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "IP_BLOCKED",
                    "message", "Your IP is blocked. Try again later."
            ));
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            return userService.findByUsername(loginRequest.getUsername())
                    .map(user -> {
                        if (!user.isAccountNonLocked()) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                                    "error", "ACCOUNT_LOCKED",
                                    "message", "Account is locked."
                            ));
                        }

                        user.setLastLoginDate(loginRequest.getTimestamp());
                        user.resetFailedLoginAttempts();
                        userService.update(user);

                        String accessToken = authService.generateJwtAccessToken(user.getUsername(), user.getAuthorities());
                        String refreshToken = authService.generateJwtRefreshToken(user.getUsername(), user.getAuthorities());

                        return ResponseEntity.ok(Map.of(
                                "username", user.getUsername(),
                                "role", user.getRole().getName(),
                                "accessToken", accessToken,
                                "refreshToken", refreshToken
                        ));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "error", "USER_NOT_FOUND",
                            "message", "Account does not exist."
                    )));
        } catch (Exception e) {
            logger.severe("Login error: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    private void enrichUserWithLocation(User user) {
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update user location", e);
        }
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            if (userService.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists.");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(new Role("USER"));
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiryTime(Date.from(LocalDateTime.now().plusHours(2).atZone(ZoneId.systemDefault()).toInstant()));

            enrichUserWithLocation(user);

            userService.saveUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during user registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    e.getCause() + ": " + e.getLocalizedMessage()
            );
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody String email) {
        try {
            Optional<User> optionalUser = userService.findByEmail(email);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the provided email not found.");
            }

            User user = optionalUser.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiryTime(Date.from(LocalDateTime.now().plusHours(2).atZone(ZoneId.systemDefault()).toInstant()));
            userService.update(user);

            String resetLink = "https://localhost:3000/reset-password?token=" + resetToken;
            emailService.sendEmail(user.getEmail(), "Password Reset Request",
                    String.format("Hello %s,\n\nUse the following link to reset your password:\n%s\n\nThis link will expire in 2 hours.",
                            user.getUsername(), resetLink));

            return ResponseEntity.ok("Password reset link sent to " + email);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during password reset: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            String username = authentication.getName();
            boolean tokensInvalidated = authService.validateJwtToken(username);

            SecurityContextHolder.clearContext();

            if (tokensInvalidated) {
                return ResponseEntity.ok("Successfully logged out");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to log out");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during logout process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String username = authService.validateJwtRefreshToken(refreshTokenRequest.getRefreshToken());
            List<GrantedAuthority> authorities = userService.getAuthoritiesByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String newAccessToken = authService.generateJwtAccessToken(username, authorities);
            String newRefreshToken = authService.generateJwtRefreshToken(username, authorities);
            String role= userService.getAuthoritiesByUsername(username).getFirst().getAuthority();

            return ResponseEntity.ok(Map.of(
                    "username", username,
                    "role",role,
                    "message", "Successfully refreshed security context",
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken
            ));
        } catch (ExpiredJwtException ex) {
            logger.log(Level.WARNING, "Refresh token expired", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired refresh token");
        } catch (JwtException ex) {
            logger.log(Level.WARNING, "Invalid refresh token", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired refresh token");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error during refreshing security context", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
