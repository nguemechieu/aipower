package com.sopotek.aipower.routes.auth;

import com.sopotek.aipower.model.Role;
import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.AuthService;
import com.sopotek.aipower.service.EmailService;
import com.sopotek.aipower.service.IPBlockService;
import com.sopotek.aipower.service.UserService;
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
 */
@RestController
@RequestMapping("/api/v3/auth")
@Getter
@Setter
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;

    private IPBlockService ipBlockService;

    /**
     * Constructor for dependency injection.
     *
     * @param userService      Service to handle user-related operations.
     * @param authService      Service for authentication logic.
     * @param passwordEncoder  Service for password encryption.
     * @param emailService     Service to send emails.
     */
    @Autowired
    public AuthController(IPBlockService ipBlockService,UserService userService, AuthService authService,
                          PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authService = authService;
        this.ipBlockService = ipBlockService;
        logger.info("AuthController initialized");
    }

    /**
     * Handles user login requests.
     *
     * @param loginRequest The login request payload containing username and password.
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
        String clientIP = ipBlockService.getClientIP(request);

        // Check if the IP is blocked
        if (ipBlockService.isBlocked(clientIP)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your IP is blocked due to multiple failed attempts. Try again later.");
        }

        try {
            // Fetch the user and check account lock
            Optional<User> optionalUser = userService.findByUsername(loginRequest.getUsername());
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account does not exist.");
            }

            User user = optionalUser.get();

            if (!user.isAccountNonLocked()) {
                // Register a failed attempt for this IP
                ipBlockService.registerFailedAttempt(clientIP);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is locked. Contact support.");
            }

            // Validate password and handle login logic
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Register a failed attempt for this IP
                ipBlockService.registerFailedAttempt(clientIP);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
            }

            // Reset failed attempts on successful login
            user.resetFailedLoginAttempts();
            userService.update(user);

            // Generate tokens and return success response
            String accessToken = authService.generateJwtAccessToken(user);
            String refreshToken = authService.generateJwtRefreshToken(user);
            //Role attribute is name id2 to avoid hacking guessing
            return ResponseEntity.ok(
                    Map.of(  "id",user.getId(),"id2",user.getRole(),"access_token", accessToken, "refresh_token", refreshToken)
            );

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
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
            if (userService.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists.");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(new Role("USER"));
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiryTime(
                    Date.from(LocalDateTime.now().plusHours(2).atZone(ZoneId.systemDefault()).toInstant())
            );

            userService.saveUser(user);

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

            String resetLink = "https://localhost:3000/reset-password?token=" + resetToken;
            emailService.sendEmail(user.getEmail(), "Password Reset Request",
                    "Hello " + user.getUsername() + ",\n\n" +
                            "Use the following link to reset your password:\n" + resetLink + "\n\n" +
                            "This link will expire in 2 hours.\n\n" +
                            "If you did not request a password reset, please ignore this email.\n\n" +
                            "Best regards,\nSupport Team");

            return ResponseEntity.ok("Password reset link sent to " + email);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during password reset: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}


