package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
}
