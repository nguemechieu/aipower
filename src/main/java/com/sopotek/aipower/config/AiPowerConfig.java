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

    

}