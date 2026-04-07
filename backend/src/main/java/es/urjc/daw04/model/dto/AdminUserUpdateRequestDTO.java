package es.urjc.daw04.model.dto;

public record AdminUserUpdateRequestDTO(
        String username,
        String email,
        String fullName,
        String birthDate,
        String shippingAddress
) {
}
