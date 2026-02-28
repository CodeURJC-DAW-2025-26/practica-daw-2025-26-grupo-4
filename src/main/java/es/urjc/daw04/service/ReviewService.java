package es.urjc.daw04.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.urjc.daw04.model.Review;
import es.urjc.daw04.repositories.ReviewRepository;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository repository;

    public List<Review> findAll() {
        return repository.findAll();
    }

    public Optional<Review> findById(long id) {
        return repository.findById(id);
    }

    public void save(Review review) {
        repository.save(review);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Review> findByProductIdPaged(long productId, int page, int size) {
        return repository.findByProductId(productId, PageRequest.of(page, size));
    }

    public Optional<Review> findByProductIdAndUserId(Long productId, Long userId) {
        return repository.findFirstByProductIdAndUserId(productId, userId);
    }
}
