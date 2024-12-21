package com.sopotek.aipower.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@Service
public class IPBlockService {
 private GeolocationService localizationService;
    private static final int MAX_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 15;

    // Store failed attempts by IP address
    private final Map<String, Integer> failedAttempts = new HashMap<>();

    // Store blocked IP addresses with block expiry time
    private final Map<String, LocalDateTime> blockedIPs = new HashMap<>();


    /**
     * Check if the IP address is blocked.
     * @param ipAddress The client IP address.
     * @return true if the IP is blocked, false otherwise.
     */
    public boolean isBlocked(String ipAddress) {
        // Remove expired blocks
        blockedIPs.entrySet().removeIf(entry -> LocalDateTime.now().isAfter(entry.getValue()));

        // Check if IP is in the blocked list
        ip=ipAddress;
        return blockedIPs.containsKey(ipAddress);
    }

    /**
     * Register a failed login attempt for the given IP address.
     * If the IP exceeds the allowed attempts, block it.
     * @param ipAddress The client IP address.
     */
    public void registerFailedAttempt(String ipAddress) {
        failedAttempts.put(ipAddress, failedAttempts.getOrDefault(ipAddress, 0) + 1);

        if (failedAttempts.get(ipAddress) >= MAX_ATTEMPTS) {
            blockIP(ipAddress);
        }
    }

    /**
     * Block the given IP address.
     * @param ipAddress The client IP address.
     */
    private void blockIP(String ipAddress) {
        blockedIPs.put(ipAddress, LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
        failedAttempts.remove(ipAddress); // Reset failed attempts
    }

    /**
     * Extract the client's IP address from the HTTP request.
     * @param request The HTTP request.
     * @return The client IP address.
     */
    public String getClientIP(@NotNull HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0]; // Handle multiple proxies
    }
private String ip;

}
