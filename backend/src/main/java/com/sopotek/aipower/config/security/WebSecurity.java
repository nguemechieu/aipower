package com.sopotek.aipower.config.security;

import com.sopotek.aipower.repository.JpaPersistentTokenRepository;
import com.sopotek.aipower.repository.PersistentLoginDao;

import com.sopotek.aipower.repository.UserRepository;
import com.sopotek.aipower.service.GeolocationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurity {

    @Autowired
    public WebSecurity(UserRepository userService, GeolocationService geolocationService, List<String> allowedOrigins, PersistentLoginDao persistLogin) {
        this.userService = userService;
        this.geolocationService = geolocationService;
        this.allowedOrigins = allowedOrigins;
        this.persistLogin = persistLogin;
    }

    private  UserRepository userService;
    private  GeolocationService geolocationService;

    @Value("${spring.security.cors.allowed-origins}")
    private List<String> allowedOrigins;

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/applications", "/instances", "/csrf-token", "/login", "/logout",
            "/refresh-token", "/favicon", "/static/**", "/register", "/forgot-password",
            "/reset-password", "/confirm-email", "/resend-verification-email", "/admin/**",
            "/error"
    );





    private PersistentLoginDao persistLogin;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                   //     .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource())).httpBasic(
                        authentication -> authentication
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth -> auth
                        .requestMatchers( PUBLIC_ENDPOINTS.iterator().next()).permitAll()
                        .requestMatchers("/api/v3/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(
                        login -> login
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .successForwardUrl("/home")
                                .failureUrl("/login?error=true")
                                .permitAll()
                ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                ) // allow login page
                .rememberMe(rememberMe -> {
                    try {
                        rememberMe
                                .tokenRepository(persistentTokenRepository())
                                .tokenValiditySeconds(24 * 60 * 60) // 1 day
                                .userDetailsService(userDetailsService());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addFilterBefore(new JwtRequestFilter(userService, new JwtUtil()), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() throws IOException, InterruptedException {return new UserDetailsServiceImpl(userService, passwordEncoder());}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type",
                "Authorization"
                // "X-XSRF-TOKEN"
                , "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
               UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {return new JpaPersistentTokenRepository(persistLogin);}
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {return authenticationConfiguration.getAuthenticationManager();}
}
