package com.sopotek.aipower.routes.api.auth;

import com.sopotek.aipower.config.security.GeolocationService;
import com.sopotek.aipower.domain.Role;
import com.sopotek.aipower.domain.User;
import com.sopotek.aipower.repository.UserRepository;
import com.sopotek.aipower.config.security.JwtUtil;
import com.sopotek.aipower.service.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
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
@RestController("/api/v3")
@AllArgsConstructor
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());


    PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final GeolocationService localizationService;
    private final IPBlockService ipBlockService;
    private final AuthenticationManager authenticationManager;
    private UserRepository userRepository;
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
            Role role = new Role();
            role.setRoleName("ROLE_USER");
            user.setSecurityAnswer(UUID.randomUUID().toString());
            user.setLastLoginDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            user.setRegistrationDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

            user.setRoles(Set.of(role
            ));
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
    private ResponseEntity<?> getResponseEntity(String accessToken, String refreshToken, @NotNull User userDetails) {
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
            if (user.getResetTokenExpiryTime() == null || user.getResetTokenExpiryTime().getTime() < new Date().getTime()) {
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
        newUser.setPassword(new BCryptPasswordEncoder().encode("admin123"));

        Role role = new Role();
        role.setRoleName("USER");
        newUser.setRoles(Set.of(role));
        newUser.setId(githubCallbackRequest.getId());

        // Generate JWT token
        String accessToken = jwtUtil.generateToken(user.getUsername());

        // Prepare response
        Map<String, Object> responsePayload = Map.of(
                "username", user.getUsername(),
                "roles", user.getRoles(),
                "accessToken", accessToken
        );
        return ResponseEntity.ok(responsePayload);


    }


    // ForgotPassword
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Retrieve user by email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid email.");
        }

        User user = userOptional.get();

        // Generate reset token and expiry time
        String resetToken = UUID.randomUUID().toString();
        long resetTokenExpiryTime = new Date().getTime() + (1000 * 60 * 60 * 24); // 24 hours

        user.setResetToken(resetToken);
        user.setResetTokenExpiryTime(new Date(resetTokenExpiryTime));

        userRepository.saveOrUpdate(user);

        // Send email with reset link
        String resetLink = "http://localhost:8080/reset-password?resetToken=" + resetToken;
        emailService.sendEmail(user.getEmail(), "Reset Password", "Click here to reset your password: " + resetLink);
        return ResponseEntity.ok("Reset password link sent to your email.");


    }

    @PostMapping("/api/v3/auth/google/callback")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleRequest googleRequest) {
        String email = googleRequest.getEmail();

        // Validate email
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required.");
        }

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            // User is found, authorize the user
            User user = userOptional.get();

            // Create an authentication token for the user
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token (ensure that the token includes necessary claims)
            String accessToken = jwtUtil.generateToken(user.getUsername());

            // Return response with necessary user info and token
            return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "id", user.getId(),
                    "accessToken", accessToken,
                    "roles", user.getRoles()
            ));
        } else {
            // User not found, return unauthorized response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }
    }
}











