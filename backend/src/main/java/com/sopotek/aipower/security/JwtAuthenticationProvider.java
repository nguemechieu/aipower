package com.sopotek.aipower.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication authenticate(@NotNull Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new BadCredentialsException("Invalid JWT token");
        }

        String username = jwtUtil.extractUsername(token);

        // You could load user details here if necessary
        return new JwtAuthenticationToken(username, token, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
