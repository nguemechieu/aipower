package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Getter
@Setter
@Service
public class LocalizationService {
@Value("${aipower.geo.api.url}")
    private  String GEO_API_URL = "https://ipinfo.io/{ip}?token={token}";
   @Value("${aipower.geo.api.secret.key}")
    private  String API_TOKEN ;

    private final RestTemplate restTemplate;
    private String ipAddress;

    public LocalizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> getLocalization(HttpServletRequest request) {

        Map<String, String> map = Map.of(
                "ip", ipAddress,
                "city", "Local Network",
                "region", "N/A",
                "country", "N/A",
                "loc", "0,0"
        );
//        // Check for localhost or private IP ranges
//        if ("127.0.0.1".equals(ipAddress) || "localhost".equals(ipAddress) || isPrivateIP(ipAddress)) {
//            return Map.of(
//                    "ip", ipAddress,
//                    "city", "Local Network",
//                    "region", "N/A",
//                    "country", "N/A",
//                    "loc", "0,0"
//            );
//        }

        String geo = GEO_API_URL.replace("{token}", API_TOKEN).replace("{ip}", ipAddress);

        try {ipAddress = getClientIP(request);
            return restTemplate.getForObject(geo, map.getClass());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch localization data for IP: " + ipAddress, e);
        }
    }
    /**
     * Extracts the client IP address from the HTTP request.
     *
     * @param request The HTTP request.
     * @return The client IP address.
     */
    private String getClientIP(@NotNull HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0]; // Handle multiple proxies
    }
    /**
     * Checks if the IP address is in a private range.
     */
    private boolean isPrivateIP(@NotNull String ipAddress) {
        return ipAddress.startsWith("10.") ||
                ipAddress.startsWith("192.168.") ||
                ipAddress.startsWith("172.") && ipAddress.matches("172\\.(1[6-9]|2[0-9]|3[0-1])\\..*");
    }


}
