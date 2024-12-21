package com.sopotek.aipower.security;

import com.sopotek.aipower.repository.UserRepository;
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
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Configuration
public class SecurityConfig {

    private  UserRepository userRepository;
    private  DataSource dataSource;

    @Autowired
    public SecurityConfig(UserRepository userRepository, DataSource dataSource) {
        this.userRepository = userRepository;
        this.dataSource = dataSource;

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
        Customizer<RequestCacheConfigurer<HttpSecurity>> cacher = cache -> cache.requestCache(new HttpSessionRequestCache()); // Request cache handling
        Customizer<CsrfConfigurer<HttpSecurity>> csrf = AbstractHttpConfigurer::disable;//csrfTokenRepository(csrfTokenRepository);
        Customizer<CorsConfigurer<HttpSecurity>> cors = cores -> cores.configurationSource(corsConfigurationSource());
        Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> auth = authorize -> authorize
                // Public Endpoints
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                // Role-Based Endpoints
                .requestMatchers("/api/v3/users/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(
                        "/api/v3/employee/**"
             ).hasAnyRole("ADMIN", "EMPLOYEE")

                .requestMatchers( "/api/v3/manager/**").hasAnyRole("ADMIN", "MANAGER")

                // Authenticated Endpoints
                .requestMatchers("/api/v3/**").authenticated()

                // Any other request needs authentication
                .anyRequest().permitAll();

        Customizer<HeadersConfigurer<HttpSecurity>> headers = header -> {header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin); // Same-origin policy
        header.configure(http);
        header.xssProtection(
                Customizer.withDefaults()
        );

        };


        Customizer<OAuth2ClientConfigurer<HttpSecurity>> oauth2Client = oauth -> oauth.clientRegistrationRepository(clientRegistrationRepository());

        http.csrf(csrf) // CSRF protection with cookies
                .cors(cors) // CORS configuration
                .requestCache(cacher) // Request cache handling
                .userDetailsService(userDetailsService())
                .headers(headers)

                .httpBasic(
                        Customizer.withDefaults()
                )

                          .rememberMe(rememberMe -> rememberMe
                        .tokenRepository(persistentTokenRepository()) // Persistent token repository for remember-me functionality
                        .tokenValiditySeconds(24 * 60 * 60) // 1 day validity
                        .rememberMeParameter("rememberMe") // Parameter to trigger remember-me functionality
                        .userDetailsService(userDetailsService())) // User details service for remember-me
                .formLogin(login -> login.loginPage("/")
                    .successForwardUrl("/api/v3/users/{id}")
                    .failureUrl("/login?error=true")
                    .permitAll())
             //  .oauth2Client(oauth2Client)
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
                        })

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .authorizeHttpRequests(auth)
                .logout(logout -> logout.permitAll()
                        .logoutSuccessUrl("/login") // Redirect on logout
                        .deleteCookies("JSESSIONID")) // Delete session cookie on logout

                .addFilterBefore(new JwtRequestFilter(userDetailsService()), UsernamePasswordAuthenticationFilter.class); // Apply JWT filter first

        return http.build();
    }

    // OAuth2 Client Registration for Google OAuth2 authentication
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String baseUrl;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUrl;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleClientRegistration = ClientRegistration
                .withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("id")
                .redirectUri(redirectUrl)
                .authorizationGrantType(new AuthorizationGrantType("authorization_code"))
                .clientName("AiPower")
                .registrationId(clientId)
                .issuerUri("https://www.googleapis.com/auth/google")
                .userNameAttributeName("id")
                .clientAuthenticationMethod(new ClientAuthenticationMethod("authorization_code"))
                .userInfoAuthenticationMethod(new AuthenticationMethod("email"))
                .build();

        return new InMemoryClientRegistrationRepository(googleClientRegistration);
    }

    // UserDetailsService bean to load users from the repository
    @Bean
    public UserDetailsService userDetailsService() {
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
