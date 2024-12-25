package com.sopotek.aipower.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "news")
public class News implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private int totalResults;
    private String country;
    private LocalDate date; // Consider storing as a `LocalDate` for better type safety.
    private String impact;
    private String forecast;
    private String previous;
    private String title;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles=new ArrayList<>();

    // Default Constructor
    public News() {
    }

    // Constructor for general news fields
    public News(String country, LocalDate date, String impact, String title, String previous, String forecast) {
        this.country = country;
        this.date = date;
        this.impact = impact;
        this.title = title;
        this.previous = previous;
        this.forecast = forecast;
    }

    // Constructor for a complete news object
    public News(String status, int totalResults) {
        this.status = status;
        this.totalResults = totalResults;

    }

    @Override
    public String toString() {
        return "News{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", impact='" + impact + '\'' +
                ", forecast='" + forecast + '\'' +
                ", previous='" + previous + '\'' +
                ", title='" + title + '\'' +
                ", articles=" + articles +
                '}';
    }


    @Getter
    @Setter
    @Entity
    @Table(name = "article_table")
    public static class Article implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "news_id")
        private News news;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @JoinColumn(name = "source_id")
        private Source source;

        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;
        private String content;

        // Default Constructor
        public Article() {
        }

        // Constructor
        public Article(Source source, String author, String title, String description, String url,
                       String urlToImage, String publishedAt, String content) {
            this.source = source;
            this.author = author;
            this.title = title;
            this.description = description;
            this.url = url;
            this.urlToImage = urlToImage;
            this.publishedAt = publishedAt;
            this.content = content;
        }

        @Override
        public String toString() {
            return "Article{" +
                    "id=" + id +
                    ", source=" + source +
                    ", author='" + author + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    ", urlToImage='" + urlToImage + '\'' +
                    ", publishedAt='" + publishedAt + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }

        @Getter
        @Setter
        @Entity
        @Table(name = "source_table")
        public static class Source implements Serializable {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            private String name;

            // Default Constructor
            public Source() {
            }



            @Override
            public String toString() {
                return "Source{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
