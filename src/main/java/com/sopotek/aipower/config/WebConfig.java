package com.sopotek.aipower.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.datasource.url}")
String db_url ;
    CorsConfig corsConfig;
    @Value("${spring.datasource.password}")
    private String db_password;
    @Value("${spring.datasource.username}")
    private String db_username;

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataSource dataSource() {
        return DataSourceBuilder.create(

        ).url(db_url)
                .username(db_username) .password(db_password)// Replace with your database username

                .build();
    }


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
