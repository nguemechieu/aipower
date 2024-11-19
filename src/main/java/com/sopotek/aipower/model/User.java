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
@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
@Table(name = "users")
public class User extends SecurityProperties.User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String username = "defaultUsername";

    @Column(nullable = false)
    private String password = "defaultPassword";

    @Column(unique = true, nullable = false, length = 100)
    private String email = "default@example.com";

    @Column(length = 15)
    private String phoneNumber = "000-000-0000";

    @Column(length = 50)
    private String firstName = "Default";

    @Column(length = 50)
    private String middleName = "Default";

    @Column(length = 50)
    private String lastName = "User";

    private LocalDate birthdate = LocalDate.of(1985, 1, 1);

    @Column(length = 10)
    private String gender = "Unspecified";

    @Column(name = "profilePictureUrl")
    private String profilePictureUrl = "https://robohash.org/default-profile.png?size=200x200";

    @Column(name = "biography", length = 500)
    private String bio = "This is a default biography.";

    @Column(name = "role")
    private String role = "USER";

    @Column(name = "accountNonExpired")
    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "securityQuestion")
    private String securityQuestion = "What is your default security question?";

    @Column(name = "securityAnswer")
    private String securityAnswer = "DefaultAnswer";

    private boolean twoFactorEnabled = false;

    @Column(name = "accountCreationDate")
    private LocalDate accountCreationDate = LocalDate.now();

    @Column(name = "lastLoginDate")
    private LocalDate lastLoginDate = LocalDate.now();

    @Column(name = "address")
    private String address = "Default Address";

    @Column(length = 50)
    private String city = "Default City";

    @Column(length = 50)
    private String state = "Default State";

    @Column(length = 50)
    private String country = "Default Country";

    @Column(length = 10)
    private String zipCode = "00000";

    private int friendCount = 0;
    private int postCount = 0;
    private int followerCount = 0;
    private int followingCount = 0;

    private String resetToken = null;
    private LocalDateTime resetTokenExpiryTime = null;

private     boolean disable;

    @Column(name = "subscription_type", length = 20)
    private String subscriptionType = "FREE";

    public User() {
        super();
    }

    public boolean isDisabled() {
        return disable;
    }
}
