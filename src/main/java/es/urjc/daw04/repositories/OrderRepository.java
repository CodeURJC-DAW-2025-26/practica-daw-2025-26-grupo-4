package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.daw04.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
