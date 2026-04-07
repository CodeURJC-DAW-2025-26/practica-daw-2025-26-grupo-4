package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.daw04.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
