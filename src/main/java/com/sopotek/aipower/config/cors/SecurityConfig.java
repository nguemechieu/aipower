package com.sopotek.aipower.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {



        http.authorizeHttpRequests(auth -> {
            auth
                                            .requestMatchers("/api/v3/users/**").hasAnyRole("USER", "ADMIN") // Allow USER and ADMIN roles to access /api/v3/users/**
                                            .requestMatchers("/api/v3/users/admin/**").hasRole("ADMIN") // Only ADMIN role can access /api/v3/users/admin/**
                                            .requestMatchers("/api/v3/users/moderator/**").hasRole("MODERATOR") // Only MODERATOR role can access /api/v3/users/moderator/**
                                            .requestMatchers("/api/v3/users/manager/**").hasRole("MANAGER") // Only MANAGER role can access /api/v3/users/manager/**
                                            .requestMatchers("/api/v3/users/employee/**").hasRole("EMPLOYEE"); // Only EMPLOYEE role can access /api/v3/users/employee/**

                            // All other endpoints require authentication


// Require user role to access cart-related endpoints
auth.requestMatchers("/api/v3/admin/orders/**").hasRole("ADMIN").requestMatchers(
                    "/api/v3/admin/cart/**").hasRole("ADMIN").requestMatchers(
                    "/api/v3/admin/products/**").hasRole("ADMIN").requestMatchers(
                    "/api/v3/admin/orders/**").hasRole("ADMIN").anyRequest().permitAll(); // Require admin role to access admin-related cart-related endpoints, product-related endpoints, and order-related endpoints
            // Require an admin role to access admin-related order-related endpoints
                  });

        http.logout(
                logout -> logout.logoutUrl("/api/v3/logout") // Set logout URL
                        .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                        );

        return http.build();
    }



    @Bean
    public UserDetailsService userDetailsService() {
        // Define an in-memory user with the username 'admin' and password 'secure password123'
        UserDetails adminUser = User.withUsername("Admin")
                .password(passwordEncoder().encode("securepassword123"))
                .roles("ADMIN") // Assign roles as needed
                .build();

        return new InMemoryUserDetailsManager(adminUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
