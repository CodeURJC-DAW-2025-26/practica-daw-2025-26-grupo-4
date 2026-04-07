package es.urjc.daw04.model.dto;

public record AdminStatsResponseDTO(
        ChartSeriesDTO salesByCategory,
        ChartSeriesDTO salesByTag,
        ChartSeriesDTO monthlySales,
        ChartSeriesDTO ordersByMonth,
        ChartSeriesDTO reviewsByMonth
) {
}
