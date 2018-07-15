package io.github.ama_csail.amaexampleapp.news;

/**
 * Representation of an article from the News API
 * You should not need to touch this file for the purposes of the project
 * @author Aaron Vontell
 */
public class Article {

    private String source;
    private String title;
    private String author;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    /**
     * Creates a article headline for display
     * @param source
     * @param title
     * @param author
     * @param description
     * @param url
     * @param urlToImage
     * @param publishedAt
     */
    public Article(String source, String title, String author, String description, String url,
                   String urlToImage, String publishedAt) {

        this.source = source;
        this.title = title;
        this.author = author;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;

    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }
}