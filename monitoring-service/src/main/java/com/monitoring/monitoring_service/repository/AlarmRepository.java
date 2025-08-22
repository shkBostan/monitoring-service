package com.monitoring.monitoring_service.repository;

import com.monitoring.monitoring_service.model.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AlarmRepository provides CRUD operations for AlarmEntity.
 * Using JpaRepository allows querying and saving alarm history easily.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    // Optionally, you can add custom query methods here
}