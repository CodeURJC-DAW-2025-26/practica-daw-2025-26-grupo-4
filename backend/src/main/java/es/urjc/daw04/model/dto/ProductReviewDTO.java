package es.urjc.daw04.model.dto;

import java.time.LocalDate;

public record ProductReviewDTO(
        long id,
        String content,
        double rating,
        LocalDate date,
        String authorName
) {}
