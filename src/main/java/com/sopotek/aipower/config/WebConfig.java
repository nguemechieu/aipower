package com.sopotek.aipower.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    CorsConfig corsConfig;

    public WebConfig(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {


        registry.addMapping("/api/**") // Adjust the mapping to match your API endpoints
                .allowedOrigins("http://localhost:3000") // Allow only your React app’s origin
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Add other methods as needed
                .allowCredentials(true).exposedHeaders(
                        "Authorization" // Add other headers as needed
                );



    }}
