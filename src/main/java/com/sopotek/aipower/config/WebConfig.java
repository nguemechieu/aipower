package com.sopotek.aipower.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;


import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }


    @Bean
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setDetectAllHandlerMappings(true);
        dispatcherServlet.setDetectAllHandlerAdapters(true);
        dispatcherServlet.setDetectAllHandlerExceptionResolvers(true);
        dispatcherServlet.setDetectAllViewResolvers(true);
        dispatcherServlet.setDispatchOptionsRequest(true);



        return dispatcherServlet;
    }
    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    @Value("${spring.datasource.url}")
    private String db_url ;
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


        registry.addMapping("/**") // Adjust the mapping to match your API endpoints
                .allowedOrigins("http://localhost:3000","http://localhost:9092","http://localhost:8081","http://localhost:8080") // Allow only your React app’s origin

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);



    }}
