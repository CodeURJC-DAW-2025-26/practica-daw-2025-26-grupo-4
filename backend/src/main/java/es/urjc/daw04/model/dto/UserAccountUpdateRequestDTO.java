package es.urjc.daw04.model.dto;

public record UserAccountUpdateRequestDTO(
        String username,
        String fullName,
        String birthDate
) {
}
