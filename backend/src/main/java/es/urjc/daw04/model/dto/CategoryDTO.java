package es.urjc.daw04.model.dto;

public record CategoryDTO(
        Long id,
        String name,
        String slug,
        String icon
) {}
