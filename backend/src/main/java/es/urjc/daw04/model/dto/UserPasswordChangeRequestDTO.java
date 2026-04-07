package es.urjc.daw04.model.dto;

public record UserPasswordChangeRequestDTO(
        String oldPassword,
        String newPassword,
        String confirmPassword
) {
}
