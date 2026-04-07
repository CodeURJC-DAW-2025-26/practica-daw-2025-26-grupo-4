package es.urjc.daw04.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteByProductId(Long productId);

    /**
     * Returns products ordered by total units sold (only items with an associated order).
     * Pass a Pageable with the desired limit, e.g. PageRequest.of(0, 6).
     */
    @Query("SELECT ci.product FROM CartItem ci WHERE ci.order IS NOT NULL GROUP BY ci.product ORDER BY SUM(ci.quantity) DESC")
    List<Product> findBestsellingProducts(Pageable pageable);

}
