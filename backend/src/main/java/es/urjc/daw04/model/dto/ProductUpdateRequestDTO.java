package es.urjc.daw04.model.dto;

import java.util.List;

public record ProductUpdateRequestDTO(
        String name,
        Double price,
        String description,
        List<String> tags,
        Long categoryId
) {
}
