package com.sopotek.aipower.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends SecurityProperties.User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String middleName;

    @Column(length = 50)
    private String lastName;

    private LocalDate birthdate;

    @Column(length = 10)
    private String gender;

    @Column(name = "profilePictureUrl")
    private String profilePictureUrl;

    @Column(length = 500)
    private String bio;

    @Column(name = "role")
    private String role;

    private boolean enabled;

    @Column(name = "securityQuestion")
    private String securityQuestion;

    @Column(name = "securityAnswer")
    private String securityAnswer;

    private boolean twoFactorEnabled;

    @Column(name = "accountCreationDate")
    private LocalDate accountCreationDate;

    @Column(name = "lastLoginDate")
    private LocalDate lastLoginDate;

    @Column(name = "address")
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 50)
    private String country;

    @Column(length = 10)
    private String zipCode;

    private int friendCount;
    private int postCount;
    private int followerCount;
    private int followingCount;
    private String resetToken;
    private LocalDateTime resetTokenExpiryTime;

    @Column(name = "subscription_type", length = 20) // New column
    private String subscriptionType;

    public User() {
        super();
    }
}
