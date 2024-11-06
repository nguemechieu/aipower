package com.sopotek.aipower;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.Db;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;


@RestController

@RequestMapping("/api/v3/auth")
public class AuthController {
    private static final Logger LOG = Logger.getLogger(AuthController.class.getName());

    private final Db dbService=new Db();


    public AuthController(){
    }
@Operation(
        summary = "Register a new user",
        description = "This endpoint allows a user to register a new account."

)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws  Exception {
        if(user==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is null");

        }
        dbService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");

    }
@Operation(
        summary = "Login a user",
        description = "This endpoint allows a user to login with their credentials."
)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "400", description = "Username or password is null"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password) {
       if(username==null || password==null){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is null");
       }


        boolean authenticated = dbService.authenticate(username, password);
        if (authenticated) {
            return ResponseEntity.ok("Authentication successful");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
@Operation(
        summary = "Refresh a user's token",
        description = "This endpoint allows a user to refresh their token."
)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "400", description = "Token is null"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/refresh")
    public ResponseEntity<String> validateToken(@RequestBody String token) {
        if(token.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing");
        }


        try {
            TokenValidator tokenValidator = new TokenValidator();
            if (tokenValidator.validateToken(token)) {
                return ResponseEntity.status(200).body(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            LOG.severe(String.format("Error during token validation: %s".formatted(e.getMessage())));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during token validation %s".formatted(e.getMessage()));
        }
    }

}