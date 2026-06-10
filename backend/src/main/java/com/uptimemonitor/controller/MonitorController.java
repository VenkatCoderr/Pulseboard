package com.uptimemonitor.controller;

import com.uptimemonitor.dto.CheckLogResponse;
import com.uptimemonitor.dto.MonitorRequest;
import com.uptimemonitor.dto.MonitorResponse;
import com.uptimemonitor.dto.MonitorStatsResponse;
import com.uptimemonitor.entity.User;
import com.uptimemonitor.service.MonitorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {

    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping
    public ResponseEntity<List<MonitorResponse>> getMonitors(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(monitorService.getMonitors(user));
    }

    @PostMapping
    public ResponseEntity<MonitorResponse> createMonitor(@AuthenticationPrincipal User user,
                                                         @Valid @RequestBody MonitorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(monitorService.createMonitor(user, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitor(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        monitorService.deleteMonitor(user, id);
        return ResponseEntity.noContent().build();
    }   

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<CheckLogResponse>> getLogs(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return ResponseEntity.ok(monitorService.getMonitorLogs(user, id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<MonitorStatsResponse> getStats(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return ResponseEntity.ok(monitorService.getStats(user, id));
    }
}
    