package com.sopotek.aipower.security;

import com.sopotek.aipower.repository.UserRepository;

import com.sopotek.aipower.service.GeolocationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private  UserRepository userRepository;
    private  DataSource dataSource;
    private GeolocationService localizationService;

    @Autowired
    public SecurityConfig(GeolocationService localizationService,UserRepository userRepository, DataSource dataSource) {
        this.userRepository = userRepository;
        this.dataSource = dataSource;
        this.localizationService = localizationService;

    }
    @Value("${spring.security.cors.allowed-origins}")
    private List<String> allowedOrigins;

    // Public endpoints that are accessible without authentication
    private static final String[] PUBLIC_ENDPOINTS = {
            "/applications", "/instances", "/csrf-token", "/login", "/logout", "/refresh-token", "/register",
            "/forgot-password", "/reset-password", "/confirm-email", "/resend-verification-email",
            "/admin/applications", "/admin/instances", "/admin/users", "/admin/logs", "/admin/audit-logs",
            "/admin/audit-logs/{id}", "/admin/roles", "/admin/permissions", "/admin/permissions/{id}",
            "/admin/roles/{id}/permissions", "/admin/roles/{id}/permissions/{permissionId}",
            "/admin/roles/{id}/applications", "/admin/roles/{id}/applications/{applicationId}",
            "/admin/roles/{id}/instances", "/admin/roles/{id}/instances/{instanceId}", "/admin/logs/{id}",
            "/admin/audit-logs/{id}", "/admin/users/{id}/roles", "/admin/users/{id}/roles/{roleId}",
            "/admin/users/{id}/applications"
    };


    // CORS configuration for frontend app
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-XSRF-TOKEN", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
       configuration.setAllowCredentials(true); // Allow credentials for cross-origin requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF token repository configuration
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieName("XSRF-TOKEN");
        csrfTokenRepository.setParameterName("_csrf");
        csrfTokenRepository.setCookiePath("/**");
        csrfTokenRepository.setHeaderName("X-XSRF-TOKEN");

        // Updated CSRF configuration
        Customizer<CsrfConfigurer<HttpSecurity>> csrf = csrf1 -> csrf1.csrfTokenRepository(csrfTokenRepository);

        Customizer<CorsConfigurer<HttpSecurity>> cors = cores -> cores.configurationSource(corsConfigurationSource());

        Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> auth = authorize -> authorize
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers("/api/v3/users/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/v3/employee/**").hasAnyRole("ADMIN", "EMPLOYEE")
                .requestMatchers("/api/v3/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/v3/**").authenticated()
                .anyRequest().permitAll();

        http.csrf(csrf)
                .cors(cors)
                .userDetailsService(userDetailsService())
                .httpBasic(Customizer.withDefaults())
                .rememberMe(rememberMe -> {
                    try {
                        rememberMe
                                .tokenRepository(persistentTokenRepository())
                                .tokenValiditySeconds(24 * 60 * 60)
                                .rememberMeParameter("rememberMe")
                                .userDetailsService(userDetailsService());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .formLogin(login -> login.loginPage("/")
                        .successForwardUrl("/api/v3/users/{id}")
                        .failureUrl("/login?error=true")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((_, response, _) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.flushBuffer();
                        })
                        .accessDeniedHandler((_, response, _) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.flushBuffer();
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth)
                .logout(logout -> logout.permitAll()
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(new JwtRequestFilter(userDetailsService()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    // UserDetailsService bean to load users from the repository
    @Bean
    public UserDetailsService userDetailsService() throws IOException, InterruptedException {


        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Persistent token repository for "Remember Me" functionality
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource); // Ensure DataSource is injected
        return tokenRepository;
    }


    // Password encoder (BCrypt) for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager bean for managing authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
