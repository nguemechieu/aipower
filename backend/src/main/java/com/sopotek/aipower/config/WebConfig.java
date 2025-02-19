package com.sopotek.aipower.config;

import de.codecentric.boot.admin.server.services.ApiMediaTypeHandler;
import de.codecentric.boot.admin.server.web.client.InstanceWebClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public InstanceWebClient instanceWebClient() {
        return InstanceWebClient.builder().build(); // Example builder pattern
    }

    @Bean
    public ApiMediaTypeHandler apiMediaTypeHandler() {
        return new ApiMediaTypeHandler(); // Example constructor or configuration
    }
    /**
     * Bean for RestTemplate to be used for making HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(isProductionEnvironment()
                        ? new String[]{"http://localhost:8080","http://localhost:3001", "http://localhost:3000","http://localhost:8081"}
                        : new String[]{"http://localhost:3000", "https://tradeadviser.org","http://localhost:3001", "http://localhost:8081"})
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")
                .allowCredentials(
                        true  // Enable CORS for all origins
                )
                .exposedHeaders("Authorization", "Content-Type")
                .maxAge(2000);

    }

    /**
     * Configure resource handlers to serve static content.
     */
    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        // Serving static resources from classpath:/static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * Optional method for production environment settings (CORS & Origins).
     * You can refine this method for better handling of CORS in different environments.
     */
    private boolean isProductionEnvironment() {
        String activeProfile = System.getProperty("spring.profiles.active");
        if (activeProfile == null) {
            activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");  // Environment variable fallback
        }
        return "prod".equalsIgnoreCase(activeProfile);
    }
    /**
     * Configure message converters for handling HTTP requests and responses.
     */
    @Override
    public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        // Adding JSON converter
        converters.add(new MappingJackson2HttpMessageConverter());

        // Adding support for text-based responses (important for SSE)
        converters.add(new org.springframework.http.converter.StringHttpMessageConverter());
    }

}
