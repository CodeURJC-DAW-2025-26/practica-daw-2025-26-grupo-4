package es.urjc.daw04.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.urjc.daw04.repositories.ReviewRepository;
import es.urjc.daw04.model.Review;
import java.util.List;
import java.util.Optional;

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
}
