package com.sopotek.aipower.security;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (userRepository.findByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }

        User user = userRepository.findByUsername(username).get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (user.isAccountNonLocked()) {
            throw new LockedException("User is locked");
        }

        if (user.isAccountNonExpired()) {
            throw new AccountExpiredException("User is expired");
        }

        if (user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Credentials have expired");
        }

        if (user.getFailedLoginAttempts() >= 5) {
            throw new LockedException("User is locked due to too many failed login attempts");
        }

        user.resetFailedLoginAttempts();

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

    }
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);

    }
}
