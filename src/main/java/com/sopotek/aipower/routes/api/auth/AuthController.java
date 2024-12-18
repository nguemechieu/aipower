package com.sopotek.aipower.routes.api.auth;

import com.sopotek.aipower.model.Role;
import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REST controller for authentication and user account management.
 * Handles login, registration, password resets, and refresh token operations.
 */
@Getter
@Setter
@RestController
@RequestMapping("/api/v3/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final LocalizationService localizationService;
    private final IPBlockService ipBlockService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, LocalizationService localizationService,
                          IPBlockService ipBlockService, UserService userService, AuthService authService,
                          PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authService = authService;
        this.ipBlockService = ipBlockService;
        this.localizationService = localizationService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;

        logger.info("AuthController initialized successfully");
    }
    @GetMapping("/csrf-token")
    public Map<String, String> csrf(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return Map.of("csrfToken", csrfToken.getToken());
    }
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestParam User loginRequest, HttpServletRequest request) {
        String clientIP = ipBlockService.getClientIP(request);
//
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

                        user.setLastLoginDate(new Date());
                        user.resetFailedLoginAttempts();
                        userService.update(user);

                        String accessToken = authService.generateJwtAccessToken(user.getUsername(), user.getAuthorities());

                        return ResponseEntity.ok(Map.of(
                                "username", user.getUsername(),
                                "role", user.getRole().getName(),
                                "accessToken", accessToken
                        ));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "error", "USER_NOT_FOUND",
                            "message", "Account does not exist."
                    )));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
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

            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String username = authService.validateJwtRefreshToken(refreshTokenRequest.getRefreshToken());
            List<GrantedAuthority> authorities = userService.getAuthoritiesByUsername(username);

            String newAccessToken = authService.generateJwtAccessToken(username, authorities);
            String role = userRepository.findByUsername(username)
                    .map(user -> user.getRole().getName())
                    .orElse("USER");

            return ResponseEntity.ok(Map.of(
                    "username", username,
                    "role", role,
                    "accessToken", newAccessToken
            ));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token expired");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during token refresh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @PostMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code) {
        try {
            // 1. Exchange the authorization code for a Google access token
            String googleAccessToken = authService.exchangeGoogleCodeForAccessToken(code);

            // 2. Fetch user information from Google using the access token
            ResponseEntity<?> googleUserInfo = authService.fetchGoogleUserInfo(googleAccessToken);
            if (!googleUserInfo.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user information from Google.");
            }
            Map<String, String> userInfo = new HashMap<>((Map) Objects.requireNonNull(googleUserInfo.getBody()));

            String email =  userInfo.get("email");
            String name = userInfo.get("name");
            String googleId = userInfo.get("sub");

            // 3. Check if the user already exists in your system
            Optional<User> existingUser = userService.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // 4. Register the user if they don't exist
                user = new User();
                user.setUsername(email);  // Using email as username
                user.setEmail(email);
                user.setFullName(name);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Generate a random password
                user.setRole(new Role("USER"));
                userService.saveUser(user);
                //Send new user with accessToken
                String accessToken = authService.generateJwtAccessToken(user.getUsername(), user.getAuthorities());
                return ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "role", user.getRole().getName(),
                        "accessToken", accessToken
                ));
            }

            // 5. Issue JWT tokens for the authenticated user
            String accessToken = authService.generateJwtAccessToken(user.getUsername(), user.getAuthorities());

            // 6. Return the JWT tokens and user details
            return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "role", user.getRole().getName(),
                    "accessToken", accessToken
            ));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during Google OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google OAuth failed.");
        }
    }

}
