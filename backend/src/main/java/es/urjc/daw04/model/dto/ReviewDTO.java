package es.urjc.daw04.model.dto;

import java.time.LocalDate;

public record ReviewDTO(
        long id,
        Long productId,
        Long userId,
        String content,
        double rating,
        LocalDate date,
        String authorName
) {}
