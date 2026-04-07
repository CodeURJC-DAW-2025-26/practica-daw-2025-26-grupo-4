package es.urjc.daw04.model.dto;

public record RegisterRequestDTO(
        String fullName,
        String username,
        String email,
        String password,
        String confirmPassword
) {
}
