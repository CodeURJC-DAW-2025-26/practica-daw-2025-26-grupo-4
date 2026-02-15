package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.daw04.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
