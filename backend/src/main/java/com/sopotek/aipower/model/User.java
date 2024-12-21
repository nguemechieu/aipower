package com.sopotek.aipower.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
    private Long id;

    private Date lastLoginDate;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
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

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String org;
    private String postal;

    private String timezone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Role role;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private String resetToken;

    private String securityQuestion;
    private String securityAnswer;

    private Date resetTokenExpiryTime;
    private String fullName;

    // Sample ROLES Enum (You can adjust this based on your actual roles)
    public enum ROLES {
        ADMIN, USER, GUEST
    }

    /**
     * Increment the number of failed login attempts for this user.
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    /**
     * Reset the number of failed login attempts for this user.
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    /**
     * Returns a collection of granted authorities based on the user's roles.
     *
     * @return A collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    private boolean enabled;

    /**
     * Indicates whether the account is enabled and has valid roles.
     *
     * @return True if the account is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return accountNonExpired && accountNonLocked &&
                roles.stream().anyMatch(role ->
                        Arrays.stream(ROLES.values())
                                .map(Enum::name)
                                .anyMatch(role.getName()::equals));
    }

    // Overridden methods for UserDetails interface
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
        String[] names = name.split(" ");
        this.firstName = names[0];
        this.lastName = names.length > 1 ? names[names.length - 1] : "";
        this.middleName = names.length > 2 ? String.join(" ", Arrays.copyOfRange(names, 1, names.length - 1)) : "";
        this.fullName = name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    private String language;
    private String currency;
}
