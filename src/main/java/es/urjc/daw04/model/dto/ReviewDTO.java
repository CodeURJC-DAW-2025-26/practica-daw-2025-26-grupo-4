package es.urjc.daw04.model.dto;

import java.time.LocalDate;

public record ReviewDTO(
        long id,
        Long productId,
        String content,
        double rating,
        LocalDate date,
        String authorName
) {}
