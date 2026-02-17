package es.urjc.daw04.model;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Product product;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private double rating;

    protected Review() {
    }

    public Review(Product product, String author, String content, double rating) {
        this.product = product;
        this.author = author;
        this.content = content;
        this.rating = rating;
    }

    public List<String> getStars() {
        List<String> stars = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            // fa = font-awesome
            if (this.rating >= i) {
                stars.add("fa-solid fa-star");
            } else if (this.rating >= i - 0.5) {
                stars.add("fa-solid fa-star-half-stroke");
            } else {
                stars.add("fa-regular fa-star");
            }
        }
        return stars;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
