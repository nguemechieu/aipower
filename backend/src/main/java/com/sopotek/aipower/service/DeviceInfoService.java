package com.sopotek.aipower.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;


import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceInfoService {

    /**
     * Extracts device information from the User-Agent header.
     *
     * @param request HttpServletRequest to extract User-Agent from.
     * @return A map containing device information.
     */
    public Map<String, String> getDeviceInfo(HttpServletRequest request) {
        Map<String, String> deviceInfo = new HashMap<>();

        try {
            // Get the User-Agent header
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null) {
                deviceInfo.put("error", "User-Agent header is missing.");
                return deviceInfo;
            }

            // Parse the User-Agent using UAParser
            Parser parser = new Parser();
            Client client = parser.parse(userAgent);

            // Populate device information
            deviceInfo.put("device", client.device.family); // Device type or family
            deviceInfo.put("os", client.os.family + " " + client.os.major); // Operating system
            deviceInfo.put("browser", client.userAgent.family); // Browser
            deviceInfo.put("browserVersion", client.userAgent.major); // Browser version


        } catch (Exception e) {
            deviceInfo.put("error", "Failed to parse User-Agent: " + e.getMessage());
        }

        return deviceInfo;
    }
}
