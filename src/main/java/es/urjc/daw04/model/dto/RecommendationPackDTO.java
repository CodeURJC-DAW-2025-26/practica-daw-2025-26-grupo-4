package es.urjc.daw04.model.dto;

import java.util.List;

public record RecommendationPackDTO(
    String label,
    boolean isCombo,
    List<ProductDTO> products,
    String totalPrice
) {
    
}
