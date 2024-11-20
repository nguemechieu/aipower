package com.sopotek.aipower.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
@Value("${aipower.jwt.token-expiration-time}")
    private  long maxAge ; // 1 hour in seconds
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:8080",
                "https://tradeadviser.org",
                "http://localhost:8081",
                "http://localhost:3000",
                "localhost:9092"
        ));

        // Set allowed methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // Set allowed headers (Authorization for tokens, Content-Type for JSON, etc.)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // Set allowed credentials (true allows cookies and session storage)
        configuration.setAllowCredentials(true);

        // Enable caching for preflight requests (OPTIONS requests)
        configuration.setExposedHeaders(List.of("Authorization"));

        // Cache preflight request for 1 hour
        configuration.setMaxAge(maxAge);

        // Apply this configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
