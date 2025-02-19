package com.sopotek.aipower.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Setter
@Getter
@Entity(name = "persistent_logins")
@Component
@Table(name ="persistent_logins")
public class PersistentLogin {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String series; // Primary key
    private String username;
    private String token;
    private Date lastUsed;

}
