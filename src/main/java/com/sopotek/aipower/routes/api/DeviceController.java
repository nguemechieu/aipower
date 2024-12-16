package com.sopotek.aipower.routes.api;

import com.sopotek.aipower.service.DeviceInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/api/v3/device")
public class DeviceController {

    private final DeviceInfoService deviceInfoService;

    @Autowired
    public DeviceController(DeviceInfoService deviceInfoService) {
        this.deviceInfoService = deviceInfoService;
    }

    /**
     * Endpoint to get device information.
     *
     * @param request HttpServletRequest to extract User-Agent header.
     * @return Device information as a JSON response.
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getDeviceInfo(HttpServletRequest request) {
        Map<String, String> deviceInfo = deviceInfoService.getDeviceInfo(request);
        return ResponseEntity.ok(deviceInfo);
    }
}
