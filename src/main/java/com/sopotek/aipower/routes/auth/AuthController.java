package com.sopotek.aipower.routes.auth;

import com.sopotek.aipower.model.Loc;
import com.sopotek.aipower.model.Role;
import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST controller for authentication and user account management.
 * Provides endpoints for login, registration, and password reset functionalities.
 * Handles user-related requests while ensuring proper validation, logging, and error handling.
 */
@RestController
@RequestMapping("/api/v3/auth")
@Getter
@Setter
public class AuthController {

    // Logger for capturing application events
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    // Services used for various operations
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final LocalizationService localizationService;
    private final IPBlockService ipBlockService;

    private String ipAddress; // Stores the client's IP address

    /**
     * Constructor for dependency injection.
     *
     * @param userService      Handles user-related operations (e.g., find, save, update).
     * @param authService      Handles authentication logic (e.g., token generation).
     * @param passwordEncoder  Encodes passwords securely.
     * @param emailService     Sends email notifications (e.g., password reset emails).
     * @param localizationService Retrieves location information based on IP.
     * @param ipBlockService   Manages IP blocking to prevent brute force attacks.
     */
    @Autowired
    public AuthController(LocalizationService localizationService, IPBlockService ipBlockService, UserService userService,
                          AuthService authService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authService = authService;
        this.ipBlockService = ipBlockService;
        this.localizationService = localizationService;
        logger.info("AuthController initialized successfully");
    }

    /**
     * Handles user login requests.
     *
     * @param loginRequest The login request payload containing username and password.
     * @param request      HTTP request object for extracting client IP.
     * @return ResponseEntity with authentication tokens or error messages.
     */
    @Operation(summary = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Account does not exist"),
            @ApiResponse(responseCode = "403", description = "Account locked or expired")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String clientIP = ipBlockService.getClientIP(request); // Extract client IP address

        // Check if the client's IP is blocked
        if (ipBlockService.isBlocked(clientIP)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "IP_BLOCKED", "message", "Your IP is blocked. Try again later."));
        }

        return userService.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    // Check if the user's account is locked
                    if (!user.isAccountNonLocked()) {
                        ipBlockService.registerFailedAttempt(clientIP);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "ACCOUNT_LOCKED", "message", "Account is locked. Contact support."));
                    }

                    // Verify the provided password
                    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        ipBlockService.registerFailedAttempt(clientIP); // Increment failed attempts
                        enrichUserWithLocation(user); // Update user location

                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "INVALID_CREDENTIALS", "message", "Invalid username or password."));
                    }

                    // Reset failed login attempts upon successful login
                    user.resetFailedLoginAttempts();
                    userService.update(user);

                    // Generate authentication tokens
                    return ResponseEntity.ok(Map.of(
                            "id", user.getId(),
                            "role", user.getRole().getName(),
                            "access_token", authService.generateJwtAccessToken(user),
                            "refresh_token", authService.generateJwtRefreshToken(user)
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "USER_NOT_FOUND", "message", "Account does not exist.")));
    }


    private void enrichUserWithLocation(User user) {
        try {
            // Delegate location update logic to the LocalizationService
            userService.saveUser(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update user location", e);
        }
    }

    /**
     * Registers a new user account.
     *
     * @param user The user details for registration.
     * @return ResponseEntity indicating success or failure.
     */
    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            // Check if the username or email already exists
            if (userService.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists.");
            }

            // Set default user attributes
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(new Role("USER"));
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiryTime(
                    Date.from(LocalDateTime.now().plusHours(2).atZone(ZoneId.systemDefault()).toInstant())
            );

            // Enrich user with location data
            enrichUserWithLocation(user);

            userService.saveUser(user); // Save user to the database

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during user registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Sends a password reset link to the user's email.
     *
     * @param email The email of the user requesting a password reset.
     * @return ResponseEntity with a success or error message.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody String email) {
        try {
            Optional<User> optionalUser = userService.findByEmail(email);

            // Check if the email exists in the system
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the provided email not found.");
            }

            User user = optionalUser.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiryTime(
                    Date.from(LocalDateTime.now().plusHours(2).atZone(ZoneId.systemDefault()).toInstant())
            );
            userService.update(user);

            // Construct and send the password reset link
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
}
