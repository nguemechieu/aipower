package com.sopotek.aipower.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.Duration;
import java.time.Instant;
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

    private String username = "defaultUsername";


    private String password = "defaultPassword";

    @Column(unique = true, nullable = false, length = 100)
    private String email = "default@example.com";

    @Column(length = 15)
    private String phoneNumber = "000-000-0000";

    private String firstName = "Default";


    private String middleName = "Default";


    private String lastName = "User";

    private LocalDate birthdate = LocalDate.of(1985, 1, 1);


    private String gender = "Male";


    private String profilePictureUrl = "https://robohash.org/default-profile.png?size=200x200";

    @Column(name = "biography", length = 500)
    private String bio = "This is a default biography.";

    @Column(name = "role")
    private String role = "USER";


    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;


    private boolean enabled = true;


    private String securityQuestion = "What is your default security question?";

    private String securityAnswer = "DefaultAnswer";

    private boolean twoFactorEnabled = false;


    private LocalDate accountCreationDate = LocalDate.now();


    private LocalDate lastLoginDate = LocalDate.now();

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

    private String resetToken = "TYTUYIUOIPO";
    private LocalDateTime resetTokenExpiryTime ;

private     boolean disable;

    @Column(name = "subscription_type", length = 20)
    private String subscriptionType = "FREE";

    public User() {
        super();
    }

    public Instant getExpiryDate() {
        // Assuming birthdate is an instance of Date or LocalDate
        if (birthdate == null) {
            throw new IllegalArgumentException("Birthdate cannot be null");
        }

        // Convert birthdate to Instant for comparison
        Instant birthdateInstant = Instant.from(birthdate);
        Instant currentInstant = Instant.now();

        // Calculate age in the past years
        long ageInYears = Duration.between(birthdateInstant, currentInstant).toDays() / 365;

        // Check if the age plus the condition (120 years) is valid
        if (ageInYears < 120) {
            return currentInstant.plus(Duration.ofDays((120 - ageInYears) * 365));
        } else {
            throw new IllegalStateException("Age exceeds the allowed limit of 120 years.");
        }
    }
}
