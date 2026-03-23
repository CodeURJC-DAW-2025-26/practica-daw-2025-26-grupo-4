package es.urjc.daw04.model.dto;

import java.util.List;

public record HomeProductsResponseDTO(
        List<ProductDTO> products,
        boolean hasMore
) {
}