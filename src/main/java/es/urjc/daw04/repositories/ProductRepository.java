package es.urjc.daw04.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.urjc.daw04.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByCategoryId(Long categoryId);

	Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

	Page<Product> findByIdNotInOrderByIdAsc(List<Long> excludedIds, Pageable pageable);

	Page<Product> findAllByOrderByIdAsc(Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.tags t WHERE p.id NOT IN :excludedIds AND (" +
			"(p.category IS NOT NULL AND p.category.id IN :categoryIds) OR " +
			"LOWER(t) IN :tags)")
	List<Product> findRecommendationCandidates(@Param("excludedIds") List<Long> excludedIds,
			@Param("categoryIds") List<Long> categoryIds,
			@Param("tags") List<String> tags,
			Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.tags t WHERE " +
			"(p.category IS NOT NULL AND p.category.id IN :categoryIds) OR " +
			"LOWER(t) IN :tags")
	List<Product> findRecommendationCandidatesWithoutExclusions(@Param("categoryIds") List<Long> categoryIds,
			@Param("tags") List<String> tags,
			Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND (" +
			"LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))"
			+ ")")
	List<Product> searchByCategoryId(@Param("categoryId") Long categoryId, @Param("query") String query);

	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND (" +
			"LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))"
			+ ")")
	Page<Product> searchByCategoryIdPaged(@Param("categoryId") Long categoryId, @Param("query") String query,
			Pageable pageable);
}
