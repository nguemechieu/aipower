package com.sopotek.aipower.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private Date lastLoginDate;
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    @Column(unique = true, nullable = false, length = 100)
    private String password;
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;
    private String firstName;
    private String lastName;
    private String middleName;
    private String address;
    private String phoneNumber;
    private String country;
    private String city;
    private String state;
    private String zipCode;
    private String profilePictureUrl;
    private String bio;
    private String gender;
    private String birthdate;
    private String ip;
    private String hostname;
    private String region;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private int failedLoginAttempts;
    private String resetToken;
    private String securityQuestion;
    private String securityAnswer;
    private Date resetTokenExpiryTime;
    private boolean emailVerified;
    private String language;
    private String currency;
    private Date registrationDate;

    public User() {

    }



    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isEnabled() {
        return accountNonExpired && accountNonLocked && !roles.isEmpty();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.firstName = "";
            this.lastName = "";
            this.middleName = "";
        } else {
            String[] names = name.split(" ");
            this.firstName = names[0];
            this.lastName = names.length > 1 ? names[names.length - 1] : "";
            this.middleName = names.length > 2 ? String.join(" ", Arrays.copyOfRange(names, 1, names.length - 1)) : "";
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setSecurityAnswer(String securityAnswer) {
        // Hash the security answer before storing it for better security
        this.securityAnswer = hashSecurityAnswer(securityAnswer);
    }

    private String hashSecurityAnswer(String answer) {
        // Implement bcrypt hashing for security answers
        if (answer == null) {
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(answer);
    }

    // Pre-persist to set default lastLoginDate
    @PrePersist
    public void prePersist() {
        if (this.lastLoginDate == null) {
            this.lastLoginDate = new Date();
        }
    }

    public void setRegistrationDate(Date from) {
        this.registrationDate = Objects.requireNonNullElseGet(from, Date::new);
    }
}
