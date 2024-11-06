package com.sopotek.aipower.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {



       http .authorizeHttpRequests(auth -> {
            auth.requestMatchers("/swagger-ui/**",

                            "/api/v3/auth/**",

                            "/api-docs/**").permitAll() // Public access to documentation
                    .requestMatchers("/api/v3/users/**").hasAnyRole("USER", "ADMIN") // USER or ADMIN roles
                    .requestMatchers("/api/v3/users/admin/**").hasRole("ADMIN") // ADMIN only
                    .requestMatchers("/api/v3/users/moderator/**").hasRole("MODERATOR") // MODERATOR only
                    .requestMatchers("/api/v3/users/manager/**").hasRole("MANAGER") // MANAGER only
                    .requestMatchers("/api/v3/users/employee/**").hasRole("EMPLOYEE") // EMPLOYEE only
                    .requestMatchers("/api/v3/admin/orders/**", "/api/v3/admin/cart/**", "/api/v3/admin/products/**")
                    .hasRole("ADMIN") // ADMIN for all admin-related endpoints
                    .anyRequest().authenticated(); // All other endpoints require authentication
        });

        http.logout(logout ->
                logout.logoutUrl("/api/v3/logout")
                        .logoutSuccessUrl("/api/v3/auth/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
        );

        return http.build();
    }

}