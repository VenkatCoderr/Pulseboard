package com.uptimemonitor.dto;

import com.uptimemonitor.entity.Monitor;
import com.uptimemonitor.entity.MonitorStatus;
import java.time.Instant;
import java.util.UUID;

public record MonitorResponse(
        UUID id,
        String name,
        String url,
        boolean active,
        MonitorStatus currentStatus,
        Instant createdAt
) {
    public static MonitorResponse fromEntity(Monitor monitor) {
        return new MonitorResponse(
                monitor.getId(),
                monitor.getName(),
                monitor.getUrl(),
                Boolean.TRUE.equals(monitor.getActive()),
                monitor.getCurrentStatus(),
                monitor.getCreatedAt()
        );
    }
}
