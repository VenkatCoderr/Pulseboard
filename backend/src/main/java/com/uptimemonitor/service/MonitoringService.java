package com.uptimemonitor.service;

import com.uptimemonitor.config.AppProperties;
import com.uptimemonitor.entity.Alert;
import com.uptimemonitor.entity.AlertType;
import com.uptimemonitor.entity.CheckLog;
import com.uptimemonitor.entity.CheckStatus;
import com.uptimemonitor.entity.Monitor;
import com.uptimemonitor.entity.MonitorStatus;
import com.uptimemonitor.repository.AlertRepository;
import com.uptimemonitor.repository.CheckLogRepository;
import com.uptimemonitor.repository.MonitorRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MonitoringService {

    private final MonitorRepository monitorRepository;
    private final CheckLogRepository checkLogRepository;
    private final AlertRepository alertRepository;
    private final EmailAlertService emailAlertService;
    private final HttpClient httpClient;
    private final AppProperties appProperties;

    public MonitoringService(MonitorRepository monitorRepository,
                             CheckLogRepository checkLogRepository,
                             AlertRepository alertRepository,
                             EmailAlertService emailAlertService,
                             AppProperties appProperties) {
        this.monitorRepository = monitorRepository;
        this.checkLogRepository = checkLogRepository;
        this.alertRepository = alertRepository;
        this.emailAlertService = emailAlertService;
        this.appProperties = appProperties;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofMillis(appProperties.getMonitor().getTimeoutMs()))
                .build();
    }

    public void checkActiveMonitors() {
    List<Monitor> monitors = monitorRepository.findByActiveTrue();

    monitors.parallelStream()
            .forEach(monitor -> {
                try {
                    checkSingleMonitor(monitor);
                } catch (Exception exception) {
                    log.error("Failed to process monitor {}", monitor.getId(), exception);
                }
            });
}

    @Transactional
    public void checkSingleMonitor(Monitor monitor) {
        CheckResult result = pingUrl(monitor.getUrl());

        CheckLog checkLog = CheckLog.builder()
                .monitor(monitor)
                .status(result.status())
                .responseTimeMs(result.responseTimeMs())
                .statusCode(result.statusCode())
                .build();
        checkLogRepository.save(checkLog);

        MonitorStatus previousStatus = monitor.getCurrentStatus();
        MonitorStatus currentStatus = result.status() == CheckStatus.UP ? MonitorStatus.UP : MonitorStatus.DOWN;

        if (previousStatus != currentStatus) {
            // Persist the latest state immediately so the dashboard reflects the newest signal.
            monitor.setCurrentStatus(currentStatus);
            monitorRepository.save(monitor);
            triggerAlertIfNeeded(monitor, previousStatus, currentStatus, result.statusCode(), result.responseTimeMs());
        }
    }

    private CheckResult pingUrl(String url) {
        long start = System.nanoTime();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(appProperties.getMonitor().getTimeoutMs()))
                    .header("User-Agent", "UptimeMonitorBot/1.0")
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            long responseTimeMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

            CheckStatus status = response.statusCode() >= 200 && response.statusCode() < 400
                    ? CheckStatus.UP
                    : CheckStatus.DOWN;

            return new CheckResult(status, responseTimeMs, response.statusCode());
        } catch (Exception exception) {
            long responseTimeMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
            log.warn("Monitor check failed for {}: {}", url, exception.getMessage());
            return new CheckResult(CheckStatus.DOWN, responseTimeMs, null);
        }
    }

    private void triggerAlertIfNeeded(Monitor monitor,
                                      MonitorStatus previousStatus,
                                      MonitorStatus currentStatus,
                                      Integer statusCode,
                                      long responseTimeMs) {
        AlertType alertType = null;

        if (previousStatus == MonitorStatus.UP && currentStatus == MonitorStatus.DOWN) {
            alertType = AlertType.DOWN;
        } else if (previousStatus == MonitorStatus.DOWN && currentStatus == MonitorStatus.UP) {
            alertType = AlertType.RECOVERED;
        }

        if (alertType == null) {
            return;
        }

        try {
            // Alerts are sent only for real transitions, not for the initial UNKNOWN baseline.
            emailAlertService.sendAlert(monitor, alertType, statusCode, responseTimeMs);
            Alert alert = Alert.builder()
                    .monitor(monitor)
                    .alertType(alertType)
                    .build();
            alertRepository.save(alert);
        } catch (MailException exception) {
            log.error("Alert email failed for monitor {}", monitor.getId(), exception);
        }
    }

    private record CheckResult(CheckStatus status, long responseTimeMs, Integer statusCode) {
    }
}
