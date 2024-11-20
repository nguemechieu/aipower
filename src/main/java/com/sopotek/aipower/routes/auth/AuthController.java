package com.sopotek.aipower.routes.auth;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.AuthService;
import com.sopotek.aipower.service.EmailService;
import com.sopotek.aipower.service.JwtService;
import com.sopotek.aipower.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
@Getter
@Setter
@RestController


public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;


    @Autowired
    public AuthController(JwtService jwtService, UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
        logger.info("LoginController initialized");
    }

@Getter
@Setter
public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        public @NotBlank(message = "Username is required") String getUsername() {
            return username;
        }

        public void setUsername(@NotBlank(message = "Username is required") String username) {
            this.username = username;
        }

        public @NotBlank(message = "Password is required") String getPassword() {
            return password;
        }



        public LoginRequest() {
        }

        @NotBlank(message = "Password is required")
        private String password;

        private boolean rememberMe;
    }

@Operation(
        summary = "User login"
)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })

@PostMapping("/api/v3/auth/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    // Validate username and password
    if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password are required");
    }

    // Authenticate user
    Optional<User> optionalUser = userService.getUserRepository()
            .findByUsernameAndPassword(loginRequest.getUsername(), passwordEncoder.encode(loginRequest.getPassword()));

    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    User user = optionalUser.get();

    // Generate tokens
    String accessToken = jwtService.generateToken(user.getUsername());
    String refreshToken = jwtService.generateToken(user.getUsername());

    return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken
    ));
}

    protected PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Register new user"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "409", description = "Username or email already exists"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }

    )
    @PostMapping("/api/v3/auth/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (user == null || user.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request. All user fields are required.");
        }

        // Check if username exists
        Optional<User> existingUser = userService.getUserRepository()
                .findByUsernameAndPassword(user.getUsername(),passwordEncoder.encode(user.getPassword()));
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        // Check if email exists
        Optional<User> existingEmail = userService.getUserRepository()
                .findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
        }

        // Set default values for new user
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        newUser.setEnabled(true); // Enable the user by default
        newUser.setRole("USER"); // Default role
        newUser.setAccountCreationDate(LocalDate.now());
        newUser.setLastLoginDate(LocalDate.now());
        // Additional fields
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setMiddleName(user.getMiddleName());
        newUser.setAddress(user.getAddress());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setSecurityQuestion(user.getSecurityQuestion());
        newUser.setSecurityAnswer(user.getSecurityAnswer());
        newUser.setTwoFactorEnabled(false);
        newUser.setResetToken(UUID.randomUUID().toString());
        newUser.setResetTokenExpiryTime(LocalDateTime.now().plusDays(7)); // Token expires in 7 days

        userService.getUserRepository().save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }


    EmailService emailService;


    // Refresh Token Endpoint
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            // Extract the token from the Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header.");
            }

            String refreshToken = authHeader.substring(7); // Extract token after "Bearer "
            String newAccessToken = jwtService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh token: " + e.getMessage());
        }
    }

    // Forgot Password Endpoint
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        // Step 1: Find user by email

        if (email==null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required.");
        }






        Optional<User> optionalUser = userService.getUserRepository().findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email " + email + " not found.");
        }

        User user = optionalUser.get();

        // Step 2: Generate a password reset token and expiry time
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(2); // Token valid for 2 hour

        user.setResetToken(resetToken);
        user.setResetTokenExpiryTime(expiryTime);

        // Save the updated user
        userService.getUserRepository().save(user);

        // Step 3: Send password reset link via email
        String resetLink = "https://tradeadviser.org/reset-password?token=" + resetToken;
        String subject = "Password Reset Request";
        String body = "Hello " + user.getUsername() + ",\n\n"
                + "We received a request to reset your password. Please use the link below to reset your password:\n\n"
                + resetLink + "\n\n"
                + "This link will expire in 1 hour.\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\nYour Application Team";

        try {
            sendEmail(user.getEmail(), subject, body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send password reset email.");
        }

        return ResponseEntity.ok("Password reset link sent to " + email);
    }

    // Helper method to send email
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);



        emailService.sendEmail(to,"RESET PASSWORD LINK",message.getText()); // Assuming `mailSender` is a configured instance of `JavaMailSender`
    }

}
