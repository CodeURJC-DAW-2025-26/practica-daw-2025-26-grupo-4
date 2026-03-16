package es.urjc.daw04.model.dto;

import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        double price,
        String description,
        List<String> tags,
        double averageRating,
        CategoryDTO category,
        List<ProductReviewDTO> reviews
) {
}
