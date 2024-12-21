package com.sopotek.aipower.routes.api;


import com.sopotek.aipower.model.Location;
import com.sopotek.aipower.service.GeolocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GeolocationController {
    private static final Logger LOG= LoggerFactory.getLogger(GeolocationController.class);
    @Autowired
    public GeolocationController(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }


     GeolocationService geolocationService;

    @GetMapping("/get-location")
    public Location getLocation() throws IOException, InterruptedException {
        return geolocationService.getUserLocation();
    }

    @GetMapping("/metrics/threads")
    public Map<String, Object> getThreadMetrics() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            System.out.println("Deadlocked threads detected:");
            for (long threadId : deadlockedThreads) {
                ThreadInfo info = threadMXBean.getThreadInfo(threadId);
                LOG.info("Thread Name: {}", info.getThreadName());
              LOG.info("Thread State: {}", info.getThreadState());
            }
        }


        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalThreads", threadMXBean.getThreadCount());
        metrics.put("daemonThreads", threadMXBean.getDaemonThreadCount());
        metrics.put("peakThreads", threadMXBean.getPeakThreadCount());
        metrics.put("totalStartedThreads", threadMXBean.getTotalStartedThreadCount());
        return metrics;
    }
    @GetMapping("/csrf-token")
    public ResponseEntity<String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return ResponseEntity.ok(csrfToken.getToken());
    }
    /**
     * Endpoint to get user localization based on IP address.
     *

     * @return ResponseEntity containing localization data.
     */
    @GetMapping("/api/v3/localization")
    public ResponseEntity<?> getUserLocalization() throws IOException, InterruptedException {

        Location localization = geolocationService.getUserLocation();
        return ResponseEntity.ok(localization);
    }

}
