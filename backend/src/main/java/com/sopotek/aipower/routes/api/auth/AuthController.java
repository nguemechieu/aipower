package com.sopotek.aipower.routes.api.auth;

import com.sopotek.aipower.domain.Role;
import com.sopotek.aipower.domain.User;
import com.sopotek.aipower.repository.UserRepository;
import com.sopotek.aipower.config.security.JwtUtil;
import com.sopotek.aipower.service.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.Getter;
import lombok.Setter;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private final GeolocationService localizationService;
    private final IPBlockService ipBlockService;
    private final AuthenticationManager authenticationManager;
    private  UserRepository userRepository;
    private JwtUtil jwtUtil;




        @Autowired
        public AuthController(AuthenticationManager authenticationManager, GeolocationService localizationService,
                              IPBlockService ipBlockService, AuthService authService,
                              PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository, JwtUtil jwtUtil) {
            this.passwordEncoder = passwordEncoder;
            this.emailService = emailService;
            this.authService = authService;
            this.ipBlockService = ipBlockService;
            this.localizationService = localizationService;
            this.authenticationManager = authenticationManager;
            this.userRepository = userRepository;
            this.jwtUtil = jwtUtil;

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
                String userName = userDetails.getUsername();
                Optional<User> user = userRepository.findByUsername(userName);
                if (user.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "error", "USER_NOT_FOUND",
                            "message", "User not found."
                    ));
                }
                // Update failed login attempts and last login timestamp
                userRepository.updateFailedLoginAttempts(0, user.get().getId());
                return getResponseEntity(jwtUtil.generateAccessToken(user.get()), jwtUtil.generateRefreshToken(user.get()), user.get());
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
                if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
                }

                if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
                }

                // Encode password
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRole(new Role("USER"));
                user.setResetToken(UUID.randomUUID().toString());
                user.setResetTokenExpiryTime(Date.from(LocalDateTime.now().plusHours(2).atZone(ZoneId.systemDefault()).toInstant()));

                // Save user
                userRepository.saveOrUpdate(user);
                return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during user registration", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
            }
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
            try {
                String username = jwtUtil.extractUsername(refreshTokenRequest.getAccessToken());

                Optional<User> user = userRepository.findByUsername(username);
                if (user.isEmpty() || !jwtUtil.validateToken(refreshTokenRequest.getAccessToken())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
                }
                return getResponseEntity(jwtUtil.generateAccessToken(user.get()), jwtUtil.generateRefreshToken(user.get()), user.get());
            } catch (ExpiredJwtException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token expired");
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during token refresh", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
            }
        }

        @NotNull
        private ResponseEntity<?> getResponseEntity(String accessToken, String refreshToken, User userDetails) {
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("username", userDetails.getUsername());
            return ResponseEntity.ok(response);
        }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            // Retrieve user by reset token
            Optional<User> userOptional = userRepository.findByResetToken(request.getResetToken());

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid reset token.");
            }

            User user = userOptional.get();

            // Validate reset token expiry
            if (user.getResetTokenExpiryTime() == null || user.getResetTokenExpiryTime().getTime()<new Date().getTime()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Reset token has expired.");
            }



            // Encrypt and update password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(request.getNewPassword());

            user.setPassword(hashedPassword);
            user.setResetToken(null); // Clear the reset token
            user.setResetTokenExpiryTime(null); // Clear the expiry time

            userRepository.saveOrUpdate(user);

            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during password reset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    //GitHub call back

    @PostMapping(
            value = "/github/callback",
            consumes = "application/json"
    )
    public ResponseEntity<?> handleGithubCallback(@Valid @RequestBody GithubCallbackRequest githubCallbackRequest) {

        // Get user from GitHub
        User user = userRepository.findById(githubCallbackRequest.getId());

                    // Create new user
                    User newUser = new User();
                    newUser.setUsername(githubCallbackRequest.getUsername());
                    newUser.setEmail(githubCallbackRequest.getEmail());
                    newUser.setFullName(githubCallbackRequest.getName());
                    newUser.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
                    newUser.setRole(new Role("USER"));
                    newUser.setId(githubCallbackRequest.getId());

        // Generate JWT token
        String accessToken = JwtUtil.generateToken(user.getUsername());

        // Prepare response
        Map<String, Object> responsePayload = Map.of(
                "username", user.getUsername(),
                "role", user.getRole().getName(),
                "accessToken", accessToken
        );
        return ResponseEntity.ok(responsePayload);




    }



}
