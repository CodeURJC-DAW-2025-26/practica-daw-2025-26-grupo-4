package es.urjc.daw04.model.dto;

import java.time.LocalDate;

public record ReviewDTO(
        long id,
        String content,
        double rating,
        LocalDate date,
        String authorName
) {}
