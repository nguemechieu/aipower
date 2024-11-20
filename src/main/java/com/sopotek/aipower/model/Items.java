package com.sopotek.aipower.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

@Entity
@Table(name = "items")
public class Items {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(nullable = false, length = 100) // Title must not be null
    private String title;

    @Column(length = 500) // Optional description with a max length
    private String description;

    @Column(nullable = false) // Price must not be null
    private Double price;

    @Column(nullable = false, length = 50) // Category must not be null
    private String category;

    @Column(name = "seller_id", nullable = false) // Seller ID must not be null
    private Long sellerId;

    @ElementCollection // To store multiple images as a collection
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id")) // Images in a separate table
    @Column(name = "image_url")
    private List<String> images;

    @Column(length = 100) // Optional location with a max length
    private String location;

    // Default constructor
    public Items() {
    }

    // Parameterized constructor
    public Items(Long id, String title, String description, Double price, String category, Long sellerId, List<String> images, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sellerId = sellerId;
        this.images = images;
        this.location = location;
    }

    // toString method for better representation
    @Override
    public String toString() {
        return "Items{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", sellerId=" + sellerId +
                ", images=" + images +
                ", location='" + location + '\'' +
                '}';
    }
}
