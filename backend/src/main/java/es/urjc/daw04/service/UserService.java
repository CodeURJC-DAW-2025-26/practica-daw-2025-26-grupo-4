package es.urjc.daw04.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.urjc.daw04.model.User;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.Review;
import es.urjc.daw04.repositories.UserRepository;
import es.urjc.daw04.repositories.ReviewRepository;
import es.urjc.daw04.repositories.OrderRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public org.springframework.data.domain.Page<User> findAllPaged(int page, int size) {
        return repository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> findByName(String name) {
        return repository.findByName(name);
    }

    public void save(User user) {
        repository.save(user);
    }

    public void deleteById(Long id) {
        List<Review> reviews = reviewRepository.findByUserId(id);
        for (Review review : reviews) {
            review.setUser(null);
            reviewRepository.save(review);
        }

        repository.findById(id).ifPresent(user -> {
            List<Order> orders = orderRepository.findByUser(user);
            for (Order order : orders) {
                order.setUser(null);
                orderRepository.save(order);
            }
        });

        repository.deleteById(id);
    }
}
