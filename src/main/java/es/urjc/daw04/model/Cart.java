package es.urjc.daw04.model;

import java.util.List;
import java.util.ArrayList;

public class Cart {
    private List<CartItem> items;
    private double shippingCost;

    public Cart() {
        this.items = new ArrayList<>();
        this.shippingCost = 4.95;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public List<CartItem> getItems() {
        return items;
    }

    public boolean isHasItems() {
        return !items.isEmpty();
    }

    public int getCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getShippingCost() {
        if (items.isEmpty()) {
            return 0.0;
        }
        return shippingCost;
    }

    public String getFormattedShippingCost() {
        return String.format("%.2f", getShippingCost());
    }

    public double getSubTotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getAmount();
        }
        return subtotal;
    }

    public String getFormattedSubTotal() {
        return String.format("%.2f", getSubTotal());
    }

    public double getTotalPrice() {
        return getSubTotal() + getShippingCost();
    }
    
    public String getFormattedTotalPrice() {
        return String.format("%.2f", getTotalPrice());
    }
}
