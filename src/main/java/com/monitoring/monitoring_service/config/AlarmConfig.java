package com.monitoring.monitoring_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AlarmConfig binds alarm-related properties from application.properties.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Configuration
@ConfigurationProperties(prefix = "alarm")
@Getter
@Setter
public class AlarmConfig {

    /**
     * Thresholds for metrics
     */
    private int cpuThresholdWarning;
    private int cpuThresholdCritical;

    private int memoryThresholdWarning;
    private int memoryThresholdCritical;

    private int requestsThresholdWarning;
    private int requestsThresholdCritical;

    /**
     * Alarm cooldown in seconds (to avoid spam)
     */
    private int cooldownSeconds;

    /**
     * Comma-separated list of notifiers (e.g., email,sms,console)
     */
    private String notifiers;
}
