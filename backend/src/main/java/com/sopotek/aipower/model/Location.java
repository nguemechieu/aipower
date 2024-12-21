package com.sopotek.aipower.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String readme;
    private String ip;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String org;
    private String postal;
    private String timezone;
    private boolean isAnycast;
    private boolean isMobile;
    private boolean isAnonymous;
    private boolean isSatellite;
    private boolean isHosting;
    private String asn;
    private String asnName;
    private String domain;
    private String route;
    private String type;
    private String companyName;
    private String companyDomain;
    private String companyType;
    private boolean vpn;
    private boolean proxy;
    private boolean tor;
    private boolean relay;
    private String abuseAddress;
    private String abuseCountry;
    private String abuseEmail;
    private String abuseName;
    private String abuseNetwork;
    private String abusePhone;

    public String getZipCode() {
        return postal;
    }
}
