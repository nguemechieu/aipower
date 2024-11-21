package com.sopotek.aipower.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class AiPowerConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();




    }

    @Configuration
    public static class LoggingConfig {

        @Bean
        public CommonsRequestLoggingFilter requestLoggingFilter() {
            CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
            loggingFilter.setIncludeClientInfo(true);
            loggingFilter.setIncludeQueryString(true);
            loggingFilter.setIncludeHeaders(true);
            loggingFilter.setIncludePayload(true);
            loggingFilter.setMaxPayloadLength(10000); // Increase if needed
            return loggingFilter;
        }
    }

}