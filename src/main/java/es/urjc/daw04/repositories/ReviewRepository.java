package es.urjc.daw04.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import es.urjc.daw04.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(long productId);
    Page<Review> findByProductId(long productId, Pageable pageable);
    Optional<Review> findFirstByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT FUNCTION('MONTH', r.date) as month, COUNT(r) FROM Review r WHERE FUNCTION('YEAR', r.date) = FUNCTION('YEAR', CURRENT_DATE) GROUP BY FUNCTION('MONTH', r.date) ORDER BY month")
    List<Object[]> findReviewCountByMonth();

}
