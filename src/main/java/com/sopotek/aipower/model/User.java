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

    @Setter
    @Getter
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public LocalDate getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(LocalDate accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiryTime() {
        return resetTokenExpiryTime;
    }

    public void setResetTokenExpiryTime(LocalDateTime resetTokenExpiryTime) {
        this.resetTokenExpiryTime = resetTokenExpiryTime;
    }

    public void setCreatedAt(LocalDateTime now) {
        this.accountCreationDate = LocalDate.from(now);
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.lastLoginDate = LocalDate.from(now);
    }
}