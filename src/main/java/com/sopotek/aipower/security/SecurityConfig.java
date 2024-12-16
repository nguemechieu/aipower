package com.sopotek.aipower.security;

import com.sopotek.aipower.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    // Role constants for maintainability
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_MODERATOR = "MODERATOR";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_GROUP = "GROUP";

    @Value("${aipower.jwt.secret.key}")
    private String secretKey;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserRepository userRepository;
    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserRepository userRepository, DataSource dataSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF Protection
                .csrf(AbstractHttpConfigurer::disable)//csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Authorization Configuration
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/employee/**").hasRole(ROLE_EMPLOYEE)
                        .requestMatchers("/users/manager/**").hasRole(ROLE_MANAGER)
                        .requestMatchers("/users/moderator/**").hasRole(ROLE_MODERATOR)
                        .requestMatchers("/users/me/**").hasAnyRole(ROLE_USER, ROLE_ADMIN, ROLE_OWNER, ROLE_GROUP, ROLE_MANAGER)
                        .requestMatchers("/users/admin/**").hasRole(ROLE_ADMIN)
                        .anyRequest().permitAll()
                )
                // Remember Me Configuration
                .rememberMe(remember -> remember
                        .tokenValiditySeconds(1209600) // 14 days
                        .key(secretKey)
                        .userDetailsService(userDetailsService())
                        .tokenRepository(persistentTokenRepository())
                )
                // Login Configuration
                .formLogin(login -> login
                        .loginPage("/auth/login").permitAll()


                        .successForwardUrl("/users/me")
                        .usernameParameter("username")
                        .passwordParameter("password")
                )
                // Logout Configuration
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/login?logout").permitAll()
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                )
                // Session Management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                // Exception Handling
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                            System.out.println("Unauthorized access attempt to: " + request.getRequestURI());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("Access denied to: " + request.getRequestURI() + " - Reason: " + accessDeniedException.getMessage());
                            response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied");
                        })
                )
                // JWT Authentication Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
