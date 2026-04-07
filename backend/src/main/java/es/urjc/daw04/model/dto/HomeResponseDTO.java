package es.urjc.daw04.model.dto;

import java.util.List;

public record HomeResponseDTO(
        List<CategoryDTO> categories,
        List<ProductDTO> products,
        Long selectedCategoryId,
        String selectedCategoryName,
        String searchQuery,
        boolean hasMore
) {
}