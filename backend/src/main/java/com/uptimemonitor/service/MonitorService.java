package com.uptimemonitor.service;

import com.uptimemonitor.dto.CheckLogResponse;
import com.uptimemonitor.dto.MonitorRequest;
import com.uptimemonitor.dto.MonitorResponse;
import com.uptimemonitor.dto.MonitorStatsResponse;
import com.uptimemonitor.entity.CheckLog;
import com.uptimemonitor.entity.CheckStatus;
import com.uptimemonitor.entity.Monitor;
import com.uptimemonitor.entity.MonitorStatus;
import com.uptimemonitor.entity.User;
import com.uptimemonitor.exception.ResourceNotFoundException;
import com.uptimemonitor.repository.AlertRepository;
import com.uptimemonitor.repository.CheckLogRepository;
import com.uptimemonitor.repository.MonitorRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final CheckLogRepository checkLogRepository;
    private final AlertRepository alertRepository;

    public MonitorService(MonitorRepository monitorRepository,
                          CheckLogRepository checkLogRepository,
                          AlertRepository alertRepository) {
        this.monitorRepository = monitorRepository;
        this.checkLogRepository = checkLogRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public List<MonitorResponse> getMonitors(User user) {
        return monitorRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(MonitorResponse::fromEntity)
                .toList();
    }

    @Transactional
    public MonitorResponse createMonitor(User user, MonitorRequest request) {
        Monitor monitor = Monitor.builder()
                .user(user)
                .name(request.name().trim())
                .url(request.url().trim())
                .active(Boolean.TRUE)
                .currentStatus(MonitorStatus.UNKNOWN)
                .build();

        return MonitorResponse.fromEntity(monitorRepository.save(monitor));
    }

    @Transactional
    public void deleteMonitor(User user, UUID monitorId) {
        Monitor monitor = getOwnedMonitor(user, monitorId);
        alertRepository.deleteByMonitor(monitor);
        checkLogRepository.deleteByMonitor(monitor);
        monitorRepository.delete(monitor);
    }

    @Transactional(readOnly = true)
    public List<CheckLogResponse> getMonitorLogs(User user, UUID monitorId) {
        Monitor monitor = getOwnedMonitor(user, monitorId);
        return checkLogRepository.findByMonitorOrderByCheckedAtDesc(monitor)
                .stream()
                .map(CheckLogResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public MonitorStatsResponse getStats(User user, UUID monitorId) {
        Monitor monitor = getOwnedMonitor(user, monitorId);
        List<CheckLog> recentLogs = checkLogRepository.findTop30ByMonitorOrderByCheckedAtDesc(monitor);

        if (recentLogs.isEmpty()) {
            return new MonitorStatsResponse(0.0, 0.0, 0);
        }

        long successCount = recentLogs.stream()
                .filter(log -> log.getStatus() == CheckStatus.UP)
                .count();

        double averageResponseTime = recentLogs.stream()
                .mapToLong(CheckLog::getResponseTimeMs)
                .average()
                .orElse(0.0);

        double uptimePercentage = (successCount * 100.0) / recentLogs.size();

        return new MonitorStatsResponse(round(uptimePercentage), round(averageResponseTime), recentLogs.size());
    }

    @Transactional(readOnly = true)
    public Monitor getOwnedMonitor(User user, UUID monitorId) {
        return monitorRepository.findByIdAndUserId(monitorId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found"));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
