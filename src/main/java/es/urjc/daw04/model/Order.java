package es.urjc.daw04.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.sql.Date;


@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    private double shippingCost;
    private Date orderDate;
    private String status;

    protected Order() {
    }

    public Order(ArrayList<CartItem> items) {
        this.items = new ArrayList<>(items);
        for (CartItem item : this.items) {
            item.setOrder(this);
        }
        this.shippingCost = 4.95;
        this.orderDate = new Date(System.currentTimeMillis());
        this.status = EnumStatus.PENDING;
    }

    public void addItem(CartItem item) {
        item.setOrder(this);
        this.items.add(item);
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

    public Date getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
