package com.monitoring.monitoring_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Alarm entity represents a triggered alarm.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alarm {
    private String serviceName;
    private String metricName;
    private double value;
    private String severity; // WARNING, CRITICAL
    private LocalDateTime timestamp;
    private String message;
}
