package com.sopotek.aipower.config.security;

import com.sopotek.aipower.domain.User;
import com.sopotek.aipower.repository.UserRepository;
import com.sopotek.aipower.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
public class UserDetailsServiceImpl implements UserDetailsService {
    private  PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username).get();


    }


}
