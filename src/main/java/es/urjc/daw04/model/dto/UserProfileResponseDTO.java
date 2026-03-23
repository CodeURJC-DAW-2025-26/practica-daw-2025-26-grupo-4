package es.urjc.daw04.model.dto;

import java.time.LocalDate;
import java.util.List;

public record UserProfileResponseDTO(
        Long id,
        String username,
        String fullName,
        String email,
        LocalDate birthDate,
        String shippingAddress,
        List<String> roles,
        String profileImageUrl
) {
}
