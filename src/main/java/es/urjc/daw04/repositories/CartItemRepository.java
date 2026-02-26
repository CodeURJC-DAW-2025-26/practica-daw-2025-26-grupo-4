package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw04.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteByProductId(Long productId);

}
