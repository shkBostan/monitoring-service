package com.monitoring.monitoring_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController provides a simple health check endpoint
 * to verify that the Monitoring Service is running.
 *
 * Endpoint: GET /health
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * Returns a simple message indicating service health.
     * @return status message
     */
    @GetMapping
    public String health() {
        return "Monitoring Service is running!";
    }
}
