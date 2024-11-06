package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;


    String jwtSecret="q234rtyu78iu";


    int jwtExpirationMs=3600;

    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        // Check if user exists and password matches
        return user!=null && passwordEncoder.matches(password, user.getPassword());

    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.toInstant().getNano() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
