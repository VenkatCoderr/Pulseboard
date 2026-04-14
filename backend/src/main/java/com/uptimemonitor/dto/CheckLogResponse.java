package com.uptimemonitor.dto;

import com.uptimemonitor.entity.CheckLog;
import com.uptimemonitor.entity.CheckStatus;
import java.time.Instant;
import java.util.UUID;

public record CheckLogResponse(
        UUID id,
        Instant checkedAt,
        CheckStatus status,
        long responseTimeMs,
        Integer statusCode
) {
    public static CheckLogResponse fromEntity(CheckLog checkLog) {
        return new CheckLogResponse(
                checkLog.getId(),
                checkLog.getCheckedAt(),
                checkLog.getStatus(),
                checkLog.getResponseTimeMs(),
                checkLog.getStatusCode()
        );
    }
}
