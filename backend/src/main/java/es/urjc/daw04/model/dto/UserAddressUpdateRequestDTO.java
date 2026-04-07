package es.urjc.daw04.model.dto;

public record UserAddressUpdateRequestDTO(
        String street,
        String additional,
        String city,
        String province,
        String postalCode,
        String country,
        String phone
) {
}
