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

    public double getShippingCost() {
        return shippingCost;
    }

    public double getTotalPrice() {
        double total = shippingCost;
        for (CartItem item : items) {
            total += item.getAmount();
        }
        return total;
    }
}
