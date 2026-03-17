package es.urjc.daw04.model.dto;

public record CartItemDTO(
        Long id,
        ProductSummaryDTO product,
        int quantity
) {}
