package es.urjc.daw04.model.dto;

public record OrderItemDTO(
    long productId,
    String name,
    int quantity,
    double price,
    String imageUrl,
    boolean canReview,
    boolean hasReview
) {}
