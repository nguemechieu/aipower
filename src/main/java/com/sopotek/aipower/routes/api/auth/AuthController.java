package com.sopotek.aipower.routes.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.model.Role;
import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import com.sopotek.aipower.security.JwtUtil;
import com.sopotek.aipower.service.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST controller for authentication and user account management.
 * Handles login, registration, password resets, and refresh token operations.
 */
@Getter
@Setter
@RestController
@RequestMapping("/api/v3")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());


    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final LocalizationService localizationService;
    private final IPBlockService ipBlockService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, LocalizationService localizationService,
                          IPBlockService ipBlockService,  AuthService authService,
                          PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository) {

        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authService = authService;
        this.ipBlockService = ipBlockService;
        this.localizationService = localizationService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;

        logger.info("AuthController initialized successfully");
    }

    @PostMapping(value = "/login")
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
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            return userRepository.findByUsername(userDetails.getUsername())
                    .map(user -> {
                        if (!user.isAccountNonLocked()) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                                    "error", "ACCOUNT_LOCKED",
                                    "message", "Account is locked."
                            ));
                        }

                        // Update user details
                        user.setLastLoginDate(new Date());
                        userRepository.save(user);

                        String accessToken = jwt.generateToken(user.getUsername());

                        Map<String, Object> responsePayload = Map.of(
                                "username", user.getUsername(),
                                "role", user.getRole().getName(),
                                "accessToken", accessToken
                        );

                        return ResponseEntity.ok(responsePayload);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "error", "USER_NOT_FOUND",
                            "message", "Account does not exist."
                    )));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "BAD_CREDENTIALS",
                    "message", "Invalid username or password."
            ));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred. Please try again later."
            ));
        }
    }


    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {

            // Validate email format

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username"+user.getUsername()+" already exists.");
            }else

            // Check if email already exists
            if (userRepository.findByEmail( user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email "+user.getEmail()+"already exists.");
            }


            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(new Role("USER"));
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiryTime(Date.from(LocalDateTime.now()
                    .plusHours(2)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));

            // Save user using the repository
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            // Log and return a generic error response
            logger.log(Level.SEVERE, "Error during user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

JwtUtil jwt=new JwtUtil();

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String username = jwt.extractUsername(refreshTokenRequest.getAccessToken());

            User user=userRepository.findByUsername(username).orElseThrow(
                    () -> new UsernameNotFoundException(
                            "User not found with username: " + username
                    )
            );


            String newAccessToken = jwt.generateToken(username);
            String role = user.getRole().getName();


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
            // 1. Exchange the authorization code for an access token
            String googleAccessToken = authService.exchangeGoogleCodeForAccessToken(code);

            // 2. Fetch user information from Google using the access token
            ResponseEntity<?> googleUserInfoResponse = authService.fetchGoogleUserInfo(googleAccessToken);

            if (!googleUserInfoResponse.getStatusCode().is2xxSuccessful() || googleUserInfoResponse.getBody() == null) {
                return ResponseEntity.status(googleUserInfoResponse.getStatusCode())
                        .body(Map.of("error", "FETCH_USER_INFO_FAILED", "message", "Unable to fetch user information from Google."));
            }

            // Parse user information
            JsonNode userInfoJson = new ObjectMapper().readTree(googleUserInfoResponse.getBody().toString());
            String googleId = userInfoJson.path("sub").asText(); // Safe extraction
            String email = userInfoJson.path("email").asText();
            String name = userInfoJson.path("name").asText();

            if (googleId.isEmpty() || email.isEmpty() || name.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "error", "MISSING_GOOGLE_USER_INFO",
                        "message", "Invalid or incomplete user information from Google."
                ));
            }

            // 3. Check if user exists
            Optional<User> existingUserOptional = userRepository.findByEmail(email);
            User user = existingUserOptional.orElseGet(() -> {
                // 4. Register new user
                User newUser = new User();
                newUser.setUsername(email);  // Use email as username
                newUser.setEmail(email);
                newUser.setFullName(name);
                newUser.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString())); // Random password
                newUser.setRole(new Role("USER")); // Default role
                return userRepository.save(newUser);
            });

            // 5. Generate JWT token
            String accessToken = jwt.generateToken(user.getUsername());

            // 6. Prepare response
            Map<String, Object> responsePayload = Map.of(
                    "username", user.getUsername(),
                    "role", user.getRole().getName(),
                    "accessToken", accessToken
            );

            return ResponseEntity.ok(responsePayload);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error parsing Google user information", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "PARSING_ERROR",
                    "message", "Failed to process Google user information."
            ));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during Google OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "OAUTH_ERROR",
                    "message", "An unexpected error occurred during Google OAuth callback."
            ));
        }
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        return ResponseEntity.ok(
                "Error occurred. Please contact support for further assistance."+(
                        "\n\nCurrent Threads:\n" +
                                Arrays.toString(Thread.currentThread().getStackTrace())
                )
        );
    }


    //Reset -password

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            User user = userRepository.findByResetToken(request.getResetToken())
                   .orElseThrow(() -> new UsernameNotFoundException("No user found with this reset token."));

            if (request.getResetTokenExpiryTime().isBefore(Instant.from(LocalDateTime.now()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Reset token has expired.");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setResetToken( request.getResetToken());
            user.setResetTokenExpiryTime(Date.from(request.getResetTokenExpiryTime()));

            userRepository.save(user);

            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during password reset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    //Github call back

    @PostMapping(
            value = "/github/callback",
            consumes = "application/json"
    )
    public ResponseEntity<?> handleGithubCallback(@Valid @RequestBody GithubCallbackRequest githubCallbackRequest) {

        // Get user from GitHub
        User user = userRepository.findById(githubCallbackRequest.getId())
               .orElseGet(() -> {
                    // Create new user
                    User newUser = new User();
                    newUser.setUsername(githubCallbackRequest.getUsername());
                    newUser.setEmail(githubCallbackRequest.getEmail());
                    newUser.setFullName(githubCallbackRequest.getName());
                    newUser.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
                    newUser.setRole(new Role("USER"));
                    newUser.setId(githubCallbackRequest.getId());
                    return userRepository.save(newUser);
                });

        // Generate JWT token
        String accessToken = jwt.generateToken(user.getUsername());

        // Prepare response
        Map<String, Object> responsePayload = Map.of(
                "username", user.getUsername(),
                "role", user.getRole().getName(),
                "accessToken", accessToken
        );
        return ResponseEntity.ok(responsePayload);




    }



}
