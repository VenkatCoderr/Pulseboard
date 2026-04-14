package com.uptimemonitor.repository;

import com.uptimemonitor.entity.CheckLog;
import com.uptimemonitor.entity.Monitor;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckLogRepository extends JpaRepository<CheckLog, UUID> {

    List<CheckLog> findByMonitorOrderByCheckedAtDesc(Monitor monitor);

    List<CheckLog> findTop30ByMonitorOrderByCheckedAtDesc(Monitor monitor);

    void deleteByMonitor(Monitor monitor);
}
