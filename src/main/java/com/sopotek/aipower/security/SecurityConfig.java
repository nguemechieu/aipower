package com.sopotek.aipower.security;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@Configuration
public class SecurityConfig {

    @Value("${aipower.jwt.secret.key}")
    String SecretKey;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserRepository userRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserRepository userRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {



        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("users/manager/**").hasRole("MANAGER")
                        .requestMatchers("users/moderator/**").hasRole("MODERATOR")
                        .requestMatchers("/users/me**").hasAnyRole("USER", "ADMIN", "OWNER", "GROUP", "MANAGER")
                        .requestMatchers("/users/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )
                .rememberMe(
                      remember->{
                          remember.tokenValiditySeconds(1209600); // 14 days



                          remember.key(SecretKey); // Replace with your secret key

                          remember.tokenRepository(new InMemoryTokenRepositoryImpl()); // In-memory implementation, you can replace it with a persistent storage if needed

                      }
                ).formLogin(
                        login -> login.loginPage("/login")
                               .permitAll()

                               .successForwardUrl("/users/me")

                               .usernameParameter("username")
                               .passwordParameter("password")


                )

                .logout(logout -> logout.permitAll()
                                                .logoutSuccessUrl("/logout")
                                                .clearAuthentication(true)
                                                .invalidateHttpSession(true)

                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            // Convert roles from the user to GrantedAuthority
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword()) // The Password is already encoded in the DB
                    .authorities(user.getAuthorities())
                    .accountExpired(!user.isAccountNonExpired())
                    .accountLocked(!user.isAccountNonLocked())
                    .credentialsExpired(!user.isCredentialsNonExpired())
                    .disabled(!user.isEnabled())
                    .build();
        };
    }
}
