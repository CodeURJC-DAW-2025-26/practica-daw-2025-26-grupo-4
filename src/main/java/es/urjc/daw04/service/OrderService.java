package es.urjc.daw04.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Page<Order> findByUserPaged(User user, int page, int size) {
        return repository.findByUser(user, PageRequest.of(page, size));
    }

    public Optional<Order> findById(long id) {
        return repository.findById(id);
    }

    public void save(Order order) {
        repository.save(order);
    }

    public void saveOrderFromCart(Cart cart, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingCost(cart.getShippingCost());

        for (CartItem item : cart.getItems()) {
            CartItem dbItem = new CartItem(item.getProduct(), item.getQuantity());
            order.addItem(dbItem);
        }

        // Calculate total from items
        double itemsTotal = order.getItems().stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
        order.setTotalPrice(itemsTotal + order.getShippingCost());
        
        repository.save(order);
    }

    public List<Object[]> getSalesByCategory() {
        return repository.findUnitsSoldByCategory();
    }

    public List<Object[]> getMonthlySales() {
        return repository.findMonthlySales();
    }

    public List<Object[]> getSalesByTag() {
        return repository.findUnitsSoldByTag();
    }

    public List<Object[]> getOrdersCountByMonth() {
        return repository.findOrdersCountByMonth();
    }
}