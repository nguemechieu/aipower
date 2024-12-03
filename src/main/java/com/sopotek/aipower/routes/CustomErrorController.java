package com.sopotek.aipower.routes;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {


    @RequestMapping("/error")
    public ResponseEntity<?> handleError() {
        return ResponseEntity.status(500).body("An unexpected error occurred"); // Return custom error message or JSON


    }
}