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

    @ManyToOne
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    private double rating;

    private java.time.LocalDate date;

    protected Review() {
    }

    public Review(Product product, User user, String content, double rating) {
        this.product = product;
        this.user = user;
        this.content = content;
        this.rating = rating;
        this.date = java.time.LocalDate.now();
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

    // Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return (user != null) ? user.getName() : "Anónimo";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
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

    public java.time.LocalDate getDate() {
        return date;
    }

    public void setDate(java.time.LocalDate date) {
        this.date = date;
    }
}
