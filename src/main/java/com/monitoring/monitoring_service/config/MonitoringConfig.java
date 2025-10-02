package com.monitoring.monitoring_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MonitoringConfig binds properties from application.properties
 * related to monitoring service.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Configuration
@ConfigurationProperties(prefix = "monitoring")
@Getter
@Setter
public class MonitoringConfig {
    private String serviceName;
    private long metricsCollectIntervalMs;
}

