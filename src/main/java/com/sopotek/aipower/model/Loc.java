package com.sopotek.aipower.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo-coordinates")
public class Loc {

    private double latitude;
    private double longitude;
    @Id
    private Long id;

    /**
     * Default constructor.
     */
    public Loc() {
    }

    /**
     * Constructor to initialize latitude and longitude.
     *
     * @param latitude  The latitude value.
     * @param longitude The longitude value.
     */
    public Loc(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Parses a "loc" string in the format "latitude,longitude" into a Loc object.
     *
     * @param loc The "loc" string.
     * @return A Loc object.
     * @throws IllegalArgumentException If the loc string is invalid.
     */
    public static Loc fromString(String loc) {
        if (loc == null || !loc.contains(",")) {
            throw new IllegalArgumentException("Invalid loc string: " + loc);
        }

        String[] parts = loc.split(",");
        try {
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            return new Loc(latitude, longitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid loc format: " + loc, e);
        }
    }


    @Override
    public String toString() {
        return "Loc{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
