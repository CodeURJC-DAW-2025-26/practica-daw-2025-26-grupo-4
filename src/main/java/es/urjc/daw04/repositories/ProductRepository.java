package es.urjc.daw04.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw04.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByCategoryId(Long categoryId);
}
