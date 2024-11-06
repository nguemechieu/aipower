package com.sopotek.aipower.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Set allowed methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // Set allowed headers (Authorization for tokens, Content-Type for JSON, etc.)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Enable credentials if using cookies or session storage
        configuration.setAllowCredentials(true);

        // Cache preflight request for 1 hour
        configuration.setMaxAge(3600L);

        // Apply this configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
