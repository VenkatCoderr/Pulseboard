package com.uptimemonitor.dto;

public record MonitorStatsResponse(
        double uptimePercentage,
        double averageResponseTimeMs,
        int sampleSize
) {
}
