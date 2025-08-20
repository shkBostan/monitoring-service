package com.monitoring.monitoring_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Metric entity represents the metrics collected from services.
 * Each metric includes CPU usage, memory usage, request count, and timestamp.
 *
 * The entity is stored in the H2 in-memory database for MVP testing.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Entity
@Data
public class Metric {

    /**
     * Unique identifier for each metric record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private int cpu;
    private int memory;
    private int requests;
    private LocalDateTime timestamp;
}
