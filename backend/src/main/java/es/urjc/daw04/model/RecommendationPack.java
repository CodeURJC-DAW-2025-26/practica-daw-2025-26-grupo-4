package es.urjc.daw04.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Non-persistent DTO representing a recommendation unit.
 * A pack may contain a single product (standard card) or 2-3 products
 * from different categories (shown as a "Combo sugerido" card).
 */
public class RecommendationPack {

    private final List<Product> products;
    private final String label;

    public RecommendationPack(List<Product> products, String label) {
        this.products = new ArrayList<>(products);
        this.label = label;
    }

    public List<Product> getProducts() {
        return products;
    }

    /** True when this pack contains more than one product (combo card). */
    public boolean isCombo() {
        return products.size() > 1;
    }

    /**
     * Returns the single product when NOT a combo.
     * Used by Mustache via {{#singleProduct}} to render a normal product card.
     */
    public Product getSingleProduct() {
        return (!isCombo() && !products.isEmpty()) ? products.get(0) : null;
    }

    /**
     * Returns flat maps for Mustache iteration inside a combo card: {{#comboProducts}}.
     * Each map exposes: id, name, mainImage, formattedPrice.
     */
    public List<Map<String, Object>> getComboProducts() {
        List<Map<String, Object>> views = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> view = new HashMap<>();
            view.put("id", p.getId());
            view.put("name", p.getName());
            view.put("mainImage", p.getMainImage());
            view.put("formattedPrice", p.getFormattedPrice());
            views.add(view);
        }
        return views;
    }

    /** Comma-separated product IDs, used for the "add all combo to cart" JS helper. */
    public String getComboIds() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(products.get(i).getId());
        }
        return sb.toString();
    }

    public String getLabel() {
        return label;
    }

    public String getFormattedTotalPrice() {
        double total = products.stream().mapToDouble(Product::getPrice).sum();
        return String.format("%.2f", total).replace(".", ",");
    }
}
