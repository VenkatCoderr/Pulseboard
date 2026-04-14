package com.uptimemonitor.scheduler;

import com.uptimemonitor.service.MonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonitorScheduler {

    private final MonitoringService monitoringService;

    public MonitorScheduler(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Scheduled(fixedDelayString = "${app.monitor.scheduler.fixed-delay-ms:300000}")
    public void runChecks() {
        log.debug("Running scheduled monitor checks");
        monitoringService.checkActiveMonitors();
    }
}
