package es.urjc.daw04.model;

import java.util.List;

public class Product {
    private Long id;
    private String name;
    private double price;
    private String description;
    private List<String> tags;
    private List<String> images;

    public Product(Long id, String name, double price, String description, List<String> tags, List<String> images) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.tags = tags;
        this.images = images;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getImages() {
        return images;
    }

    public String getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return "";
    }

}
