package com.sopotek.aipower;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController

public class Api {

    static final Logger logger = Logger.getLogger(Api.class.getName());



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

        @GetMapping("/")
        public String dashboard() {
            return "dashboard";
        }


//Error page


    }

