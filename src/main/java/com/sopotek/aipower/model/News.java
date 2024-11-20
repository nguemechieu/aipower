package com.sopotek.aipower.model;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class News {


    String country;
    String date;
    String impact;
    String forecast;
    String previous;
    String title;
    // Getters and Setters
    private String status;
    private int totalResults;
    private List<Article> articles;

    public News() {
    }

    public News(String country, String date, String impact, String title, String previous, String forecast) {
        this.country = country;
        this.date = date;
        this.impact = impact;
        this.title = title;

        this.previous = previous;
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        return "News{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                ", articles=" + articles +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", impact='" + impact + '\'' +
                ", forecast='" + forecast + '\'' +
                ", previous='" + previous + '\'' +
                ", title='" + title + '\'' +
                ", previous='" + previous + '\'' +
                '}';
    }


    // Inner class Article
    @Setter
    @Getter
    public static class Article {
        // Getters and Setters
        private Source source;
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;
        private String content;
        public Article() {
        }

        // Constructor
        public Article(Source source, String author, String title, String description, String url, String urlToImage, String publishedAt, String content) {
            this.source = source;
            this.author = author;
            this.title = title;
            this.description = description;
            this.url = url;
            this.urlToImage = urlToImage;
            this.publishedAt = publishedAt;
            this.content = content;
        }

        // Inner class Source
        @Setter
        @Getter
        public static class Source {
            // Getters and Setters
            private String id;
            private String name;

            public Source() {
            }

            // Constructor
            public Source(String id, String name) {
                this.id = id;
                this.name = name;
            }

        }
    }



    // Constructor
    public News(String status, int totalResults, List<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }






        // Inner Source Class
        @Getter
        @Setter
        public static class Source {
            private String id;
            private String name;

            // Constructor
            public Source(String id, String name) {
                this.id = id;
                this.name = name;
            }

            // Default Constructor
            public Source() {
            }

            @Override
            public String toString() {
                return "Source{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        '}';
            }
        }

}