package es.urjc.daw04.model.dto;

import java.time.LocalDate;
import java.util.List;

public record AdminUserDTO(
        Long id,
        String username,
        String fullName,
        String email,
        LocalDate birthDate,
        String shippingAddress,
        List<String> roles,
        boolean banned,
        boolean admin
) {
}
