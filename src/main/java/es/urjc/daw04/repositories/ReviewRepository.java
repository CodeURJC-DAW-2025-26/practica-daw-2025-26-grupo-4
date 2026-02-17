package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.daw04.model.Review;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(long productId);
}
