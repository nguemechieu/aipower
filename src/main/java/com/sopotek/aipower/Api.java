package com.sopotek.aipower;
import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.Db;
import com.sopotek.aipower.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")//  Cross-Origin Resource Sharing
@RequestMapping("/api")
public class Api {

    static final Logger logger = Logger.getLogger(Api.class.getName());
    static final Db myService = new Db();

    // Dependency injection via constructor

    public Api() {
        logger.info("Api initialized");



    }
    @Autowired
    private UserService userService;
//v3/login
    // Login a user and return a JWT token
    @Operation(summary = "Login a user", description = "Login a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
        @PostMapping("/v3/auth/login")
        ResponseEntity<String> login(@RequestBody User user) {
            if (userService.authenticate(user.getUsername(), user.getPassword())) {
                String token = userService.generateToken(user.getUsername());
                return ResponseEntity.ok("Login successful. Token:"+ token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        }

    // Login a user and return a JWT token

    @Operation(summary = "Login a user", description = "Login a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/refresh")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        try {
            TokenValidator tokenValidator = new TokenValidator();
            if (tokenValidator.validateToken(token)) {
                return ResponseEntity.status(200).body(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error during token validation: %s".formatted(e.getMessage())));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during token validation %s".formatted(e.getMessage()));
        }
    }

    //Home page
    // Return a welcome message and list of all users from the database


    @Operation(summary = "Display welcome message", description = "Display a welcome message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Welcome message displayed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/v3/home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to AiPower   \n" + myService.getAllUsers().toString());
    }


//Error page

    @GetMapping("/error")
    public ResponseEntity<String> handleError() {
        String errorMessage = "An unexpected error occurred. Please try again later.";

        logger.severe("Error occurred during request handling."
        ); // Customize as needed


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }




}
