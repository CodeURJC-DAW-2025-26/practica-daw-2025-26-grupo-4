package es.urjc.daw04.model.dto;

import java.util.List;

public record RecommendationResponseDTO(
    String title,
    String subtitle,
    List<RecommendationPackDTO> recommendations
){}