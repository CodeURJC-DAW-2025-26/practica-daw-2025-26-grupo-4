package es.urjc.daw04.model.dto;

import java.util.List;

public record ProductSummaryDTO(
        Long id,
        String name,
        double price,
        String description,
        List<String> tags,
        double averageRating,
        CategoryDTO category
) {}
