package com.monitoring.monitoring_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Service health check endpoint")
public class HealthController {

    /**
     * Returns a simple message indicating service health.
     * @return status message
     */
    @Operation(
            summary = "Check service health",
            description = "Returns a simple message confirming that the Monitoring Service is running."
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy and running")
    @GetMapping
    public String health() {
        log.debug("traceId={} | GET /health called", MDC.get("traceId"));

        String message = "Monitoring Service is running!";
        log.info("traceId={} | Health check response: {}", MDC.get("traceId"), message);

        return message;
    }
}
