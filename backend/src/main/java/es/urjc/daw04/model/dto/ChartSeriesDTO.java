package es.urjc.daw04.model.dto;

import java.util.List;

public record ChartSeriesDTO(
        List<String> labels,
        List<Double> values
) {
}
