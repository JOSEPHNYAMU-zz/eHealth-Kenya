package co.ehealth.e_health;

public class News {

    public News(String author, String body, String picture, String title) {
        this.author = author;
        this.body = body;
        this.picture = picture;
        this.title = title;
    }

    private String author;
    private String body;
    private String picture;
    private String title;

    public News() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
