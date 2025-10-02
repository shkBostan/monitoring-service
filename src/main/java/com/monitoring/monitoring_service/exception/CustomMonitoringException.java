package com.monitoring.monitoring_service.exception;

/**
 * Base exception for all monitoring-specific business logic errors.
 */
public class CustomMonitoringException extends RuntimeException {
    public CustomMonitoringException(String message) {
        super(message);
    }

    public CustomMonitoringException(String message, Throwable cause) {
        super(message, cause);
    }
}