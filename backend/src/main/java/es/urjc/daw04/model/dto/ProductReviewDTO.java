package es.urjc.daw04.model.dto;

import java.time.LocalDate;

public record ProductReviewDTO(
        long id,
        Long userId,
        String content,
        double rating,
        LocalDate date,
        String authorName
) {}
