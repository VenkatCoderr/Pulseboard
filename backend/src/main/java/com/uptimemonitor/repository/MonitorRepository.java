package com.uptimemonitor.repository;

import com.uptimemonitor.entity.Monitor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitorRepository extends JpaRepository<Monitor, UUID> {

    List<Monitor> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Monitor> findByIdAndUserId(UUID id, UUID userId);

    @EntityGraph(attributePaths = "user")
    List<Monitor> findByActiveTrue();
}
