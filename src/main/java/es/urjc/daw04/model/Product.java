package es.urjc.daw04.model;

import jakarta.persistence.*; 
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

    // Constructor para la BBDD
    protected Product() {}

    // Constructor 
    public Product(String name, double price, String description, List<String> tags, List<String> images) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.tags = tags;
        this.images = images;
    }

    // Getters y Setters 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return "default-plant.jpg";
    }
}