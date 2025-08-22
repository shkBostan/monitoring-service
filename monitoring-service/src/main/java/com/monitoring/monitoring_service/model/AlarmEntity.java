package com.monitoring.monitoring_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AlarmEntity represents an alarm saved in the database.
 * This allows storing and querying the history of alarms.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Entity
@Data
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String metricName;
    private double metricValue;
    private String severity; // WARNING, CRITICAL
    private LocalDateTime timestamp;
    private String message;
}
