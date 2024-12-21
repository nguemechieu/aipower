package com.sopotek.aipower.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    public Token() {
    }

    private String token;
    private boolean revoked;
    private boolean expired;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;


}
