package es.urjc.daw04.model.dto;

import java.util.List;

public record RegisterResponseDTO(
        Long id,
        String username,
        String fullName,
        String email,
        List<String> roles
) {
}
