package com.uptimemonitor.repository;

import com.uptimemonitor.entity.Alert;
import com.uptimemonitor.entity.Monitor;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByMonitorOrderBySentAtDesc(Monitor monitor);

    void deleteByMonitor(Monitor monitor);
}
