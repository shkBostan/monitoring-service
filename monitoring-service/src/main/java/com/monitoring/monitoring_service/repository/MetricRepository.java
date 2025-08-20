package com.monitoring.monitoring_service.repository;

import com.monitoring.monitoring_service.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * MetricRepository provides CRUD operations for Metric entities.
 * Inherits standard JPA repository methods for saving, finding, and deleting metrics.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
public interface MetricRepository extends JpaRepository<Metric, Long> {
}
