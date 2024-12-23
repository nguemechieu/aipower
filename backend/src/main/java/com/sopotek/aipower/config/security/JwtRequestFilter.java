package com.sopotek.aipower.config.security;

import com.sopotek.aipower.domain.User;
import com.sopotek.aipower.repository.UserRepository;
import com.sopotek.aipower.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @Autowired
    public JwtRequestFilter(UserRepository userRepository, JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractJwtFromRequest(request);
        if (jwt != null && isValidToken(jwt)) {
            Authentication authentication = getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private boolean isValidToken(String token) {
        try {
            // Validate the token using JwtUtil.validateToken method
            // You should validate whether the token is expired, malformed, etc.
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Optional<Optional<User>> userDetails = Optional.ofNullable(userRepository.findByUsername(username));
                if (userDetails.isPresent()) {
                    return true;
                }
                // If the user is not found, reset the failed login attempts


            }
        } catch (Exception e) {
            // Handle any exceptions related to token validation,
            // e.g., ExpiredJwtException, MalformedJwtException, etc.
            return false;
        }
        return false;
    }

    private @NotNull Authentication getAuthentication(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Optional<User>> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isEmpty()) {
        return new UsernamePasswordAuthenticationToken(user, null, null);}
        Collection<? extends GrantedAuthority> authorities = user.get().get().getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}
