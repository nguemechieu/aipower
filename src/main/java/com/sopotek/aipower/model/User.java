package com.sopotek.aipower.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "users")

public class User extends SecurityProperties.User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
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

    @Getter
    @Setter
    @Column(length = 500)
    private String bio;

    @Column(name = "role")
    private String role;

    private boolean enabled;

    // Security and Account Information
    @Setter
    @Getter
    @Column(name = "securityQuestion")
    private String securityQuestion;

    @Getter
    @Setter
    @Column(name = "securityAnswer")
    private String securityAnswer;

    @Getter
    @Setter
    private boolean twoFactorEnabled;
    @Getter
    @Setter
    @Column(name = "accountCreationDate")
    private LocalDate accountCreationDate;

    @Column(name = "lastLoginDate")
    private LocalDate lastLoginDate;

    // Contact Information
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

    // Social Information
    private int friendCount;
    private int postCount;
    private int followerCount;
    private int followingCount;
    private String resetToken;
    private LocalDateTime resetTokenExpiryTime;

    public User() {
        super();
    }

    public User(String username, String password, String email, String phoneNumber, String firstName, String middleName, String lastName,
                LocalDate birthdate, String gender, String profilePictureUrl, String bio, String role,
                boolean enabled, String securityQuestion, String securityAnswer, boolean twoFactorEnabled,
                LocalDate accountCreationDate, LocalDate lastLoginDate, String address, String city,
                String state, String country, String zipCode, int friendCount, int postCount,
                int followerCount, int followingCount) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.profilePictureUrl = profilePictureUrl;
        this.bio = bio;
        this.role = role;
        this.enabled = enabled;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.twoFactorEnabled = twoFactorEnabled;
        this.accountCreationDate = accountCreationDate;
        this.lastLoginDate = lastLoginDate;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
        this.friendCount = friendCount;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                ", gender='" + gender + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", securityQuestion='" + securityQuestion + '\'' +
                ", securityAnswer='" + securityAnswer + '\'' +
                ", twoFactorEnabled=" + twoFactorEnabled +
                ", accountCreationDate=" + accountCreationDate +
                ", lastLoginDate=" + lastLoginDate +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", friendCount=" + friendCount +
                ", postCount=" + postCount +
                ", followerCount=" + followerCount +
                ", followingCount=" + followingCount +
                ", resetToken='" + resetToken + '\'' +
                ", resetTokenExpiryTime=" + resetTokenExpiryTime +
                '}';
    }




    public void setCreatedAt(LocalDateTime now) {
        this.accountCreationDate = LocalDate.from(now);
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.lastLoginDate = LocalDate.from(now);
    }
}