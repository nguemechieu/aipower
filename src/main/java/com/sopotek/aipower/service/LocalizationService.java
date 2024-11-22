package com.sopotek.aipower.service;

import lombok.Getter;
import lombok.Setter;
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

    public LocalizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get user localization based on IP address.
     *
     * @param ipAddress The IP address of the user.
     * @return A map containing localization data such as city, region, and country.
     */
    public Map getLocalization(String ipAddress) {
        try {
            return restTemplate.getForObject(GEO_API_URL, Map.class, ipAddress, API_TOKEN);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch localization data for IP: " + ipAddress+ e.getMessage());
        }
    }
}
