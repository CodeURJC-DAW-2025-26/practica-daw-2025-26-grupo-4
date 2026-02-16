package es.urjc.daw04.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    // Constructor para la BBDD
    protected Product() {
    }

    // Constructor
    public Product(String name, double price, String description, List<String> tags, List<String> images) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.tags = tags;
        this.images = images;
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double total = 0;
        for (Review r : reviews) {
            total += r.getRating();
        }
        return total / reviews.size();
    }

    public List<String> getAverageStars() {
        List<String> stars = new ArrayList<>();
        double totalRating = getAverageRating();
        for (int i = 1; i <= 5; i++) {
            // fa = font-awesome
            if (totalRating >= i) {
                stars.add("fa-solid fa-star");
            } else if (totalRating >= i - 0.5) {
                stars.add("fa-solid fa-star-half-stroke");
            } else {
                stars.add("fa-regular fa-star");
            }
        }
        return stars;
    }

    // Dentro de la clase Product

    public int getPercent5() {
        return calculatePercentage(5);
    }

    public int getPercent4() {
        return calculatePercentage(4);
    }

    public int getPercent3() {
        return calculatePercentage(3);
    }

    public int getPercent2() {
        return calculatePercentage(2);
    }

    public int getPercent1() {
        return calculatePercentage(1);
    }

    private int calculatePercentage(int starTarget) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }

        long count = 0;
        for (Review r : reviews) {
            // Redondeamos para que un 4.5 cuente como 5, o 4.2 como 4
            if (Math.round(r.getRating()) == starTarget) {
                count++;
            }
        }

        return (int) ((count * 100) / reviews.size());
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return "default-plant.jpg";
    }
}