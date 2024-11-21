package com.sopotek.aipower.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class News {
    private String status;
    private int totalResults;
    private List<Article> articles;
    private String country;
    private String date;
    private String impact;
    private String forecast;
    private String previous;
    private String title;

    // Default Constructor
    public News() {
    }

    // Constructor for general news fields
    public News(String country, String date, String impact, String title, String previous, String forecast) {
        this.country = country;
        this.date = date;
        this.impact = impact;
        this.title = title;
        this.previous = previous;
        this.forecast = forecast;
    }

    // Constructor for complete news object
    public News(String status, int totalResults, List<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
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
                '}';
    }

    @Getter
    @Setter
    public static class Article {
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

        @Override
        public String toString() {
            return "Article{" +
                    "source=" + source +
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
        public static class Source {
            private String id;
            private String name;

            // Default Constructor
            public Source() {
            }

            // Constructor
            public Source(String id, String name) {
                this.id = id;
                this.name = name;
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
}
