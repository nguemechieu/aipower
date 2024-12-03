package com.sopotek.aipower.routes;

import com.sopotek.aipower.service.LocalizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class LocalizationController {

    private final LocalizationService localizationService;

    @Autowired
    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    /**
     * Endpoint to get user localization based on IP address.
     *
     * @param request HTTP request to extract the client IP address.
     * @return ResponseEntity containing localization data.
     */
    @GetMapping("/api/v3/localization")
    public ResponseEntity<?> getUserLocalization(HttpServletRequest request) {

        Map<String, String> localization = localizationService.getLocalization(request);
        return ResponseEntity.ok(localization);
    }


}
