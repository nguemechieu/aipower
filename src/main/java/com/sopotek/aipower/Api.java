package com.sopotek.aipower;
import com.sopotek.aipower.service.Db;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v3")
public class Api {

    static final Logger logger = Logger.getLogger(Api.class.getName());


    Db myService = new Db();

    // Dependency injection via constructor

    public Api() {
        logger.info("Api initialized");



    }


    //Home page
    // Return a welcome message and list of all users from the database


    @Operation(summary = "Display welcome message", description = "Display a welcome message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Welcome message displayed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/home")
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

