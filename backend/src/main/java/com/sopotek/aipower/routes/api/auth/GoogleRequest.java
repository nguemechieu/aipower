package com.sopotek.aipower.routes.api.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoogleRequest {

    // Getters and Setters
    @NotEmpty(message = "idToken cannot be empty.")
    private String idToken;

    @NotEmpty(message = "Profile cannot be empty.")
    private String profile;

    @NotEmpty(message = "Email cannot be empty.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotEmpty(message = "Name cannot be empty.")
    private String name;

    private String picture;
    private String locale;

    @NotEmpty(message = "Family name cannot be empty.")
    private String familyName;

    @NotEmpty(message = "Given name cannot be empty.")
    private String givenName;

    private String error;

    // Default constructor
    public GoogleRequest() {}

    // Constructor with all fields
    public GoogleRequest(String idToken, String profile, String email, String name, String picture,
                         String locale, String familyName, String givenName, String error) {
        this.idToken = idToken;
        this.profile = profile;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.locale = locale;
        this.familyName = familyName;
        this.givenName = givenName;
        this.error = error;
    }

    @Override
    public String toString() {
        return "GoogleRequest{" +
                "idToken='" + idToken + '\'' +
                ", profile='" + profile + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", locale='" + locale + '\'' +
                ", familyName='" + familyName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
