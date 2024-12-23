package com.sopotek.aipower.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "tokens")
class Token implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;



    private String token;
    private boolean revoked;
    private boolean expired;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;


}
