package es.urjc.daw04.model.dto;

import java.util.List;

public record CartDTO(
        List<CartItemDTO> items,
        double shippingCost,
        int count,
        boolean hasItems
) {}
