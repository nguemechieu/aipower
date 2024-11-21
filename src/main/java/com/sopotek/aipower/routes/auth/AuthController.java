package com.sopotek.aipower.routes.auth;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.AuthService;
import com.sopotek.aipower.service.EmailService;
import com.sopotek.aipower.service.JwtService;
import com.sopotek.aipower.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v3/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;

    @Autowired
    public AuthController(JwtService jwtService, UserService userService, AuthService authService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authService = authService;
        logger.info("AuthController initialized");
    }

    @Operation(summary = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping(value = "/login", name = "login", consumes ="application/json")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {logger.severe("Login Request: " + loginRequest);

            Optional<User> optionalUser = userService.findByUsername(loginRequest.getUsername());
            if (optionalUser.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            User user = optionalUser.get();
            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not enabled");
            }

            String accessToken = jwtService.generateToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "id", user.getId(),
                    "role", user.getRole()
            ));
        } catch (Exception e) {
            logger.severe("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userService.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRole("USER");
        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiryTime(LocalDateTime.now().plusDays(7));

        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody String email) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email not found");
        }

        User user = optionalUser.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiryTime(LocalDateTime.now().plusHours(2));

        userService.saveUser(user);

        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        String subject = "Password Reset Request";
        String body = "Hello " + user.getUsername() + ",\n\n" +
                "Use the following link to reset your password:\n" + resetLink + "\n\n" +
                "This link will expire in 2 hours.\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Best regards,\nYour Team";

        emailService.sendEmail(user.getEmail(), subject, body);
        return ResponseEntity.ok("Password reset link sent to " + email);
    }
}
