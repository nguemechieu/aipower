package com.sopotek.aipower.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name ="persistentlogins")
@Component
public class PersistentLogin {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID

    private Long id;
    private String series; // Primary key
    private String username;
    private String token;
    private Date lastUsed;

}
