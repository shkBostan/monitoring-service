package com.monitoring.monitoring_service.repository;

import com.monitoring.monitoring_service.model.AlarmEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * AlarmRepository provides CRUD operations for AlarmEntity.
 * Using JpaRepository allows querying and saving alarm history easily.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    Page<AlarmEntity> findBySeverity(String severity, Pageable pageable);

    Page<AlarmEntity> findByTimestampBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<AlarmEntity> findBySeverityAndTimestampBetween(String severity, LocalDateTime from, LocalDateTime to, Pageable pageable);

    }
