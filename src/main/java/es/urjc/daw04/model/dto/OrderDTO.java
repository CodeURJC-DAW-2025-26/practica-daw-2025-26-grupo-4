package es.urjc.daw04.model.dto;

import java.util.Date;
import java.util.List;

public record OrderDTO (
    long id,
    String orderNumber,
    String date,
    String status,
    double totalPrice,
    double shippingCost,
    List<OrderItemDTO> items
) {}
