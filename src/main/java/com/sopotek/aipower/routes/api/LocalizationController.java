package com.sopotek.aipower.routes.api;

import com.sopotek.aipower.service.LocalizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.sopotek.aipower.routes.api.UsersController.logger;

@RestController
public class LocalizationController {

    private final LocalizationService localizationService;

    @Autowired
    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }
    @GetMapping("/metrics/threads")
    public Map<String, Object> getThreadMetrics() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            System.out.println("Deadlocked threads detected:");
            for (long threadId : deadlockedThreads) {
                ThreadInfo info = threadMXBean.getThreadInfo(threadId);
                logger.info("Thread Name: {}", info.getThreadName());
                logger.info("Thread State: {}", info.getThreadState());
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
     * @param request HTTP request to extract the client IP address.
     * @return ResponseEntity containing localization data.
     */
    @GetMapping("/api/v3/localization")
    public ResponseEntity<?> getUserLocalization(HttpServletRequest request) {

        Map<String, String> localization = localizationService.getLocalization(request);
        return ResponseEntity.ok(localization);
    }

}
