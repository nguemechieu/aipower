package com.sopotek.aipower.routes.auth;

import com.sopotek.aipower.service.AuthService;
import com.sopotek.aipower.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;
@Getter
@Setter
@RestController("/api/v3/auth/login")
public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public LoginController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        logger.info("LoginController initialized");
    }



@PostMapping("/api/v3/auth/login")
    public ResponseEntity<String> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        if (userService.isUserExist(username)) {
            if (userService.isUserPasswordValid(username, password)) {
                String token = userService.generateToken(username);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token); // Add the token to the Authorization header

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .headers(headers)
                        .body("Login successful");
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password");
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username does not exist");
        }
    }
}
