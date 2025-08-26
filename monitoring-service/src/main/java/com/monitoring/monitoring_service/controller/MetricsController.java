package com.monitoring.monitoring_service.controller;

import com.monitoring.monitoring_service.dto.MetricDto;
import com.monitoring.monitoring_service.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MetricsController provides a simple endpoint for returning sample metrics.
 * This is used internally by the MetricsCollector service for testing purposes.
 * The endpoint returns CPU, memory, and request counts as random integers.
 *
 * @since Aug, 2025
 * @author s Bostan
 */

@Slf4j
@RestController
@Tag(name = "Metrics", description = "Endpoints for collecting and viewing metrics")
public class MetricsController {

    @Autowired
    MetricService metricService;


    /**
     * Returns random sample metrics.
     * Endpoint: GET /metrics
     *
     * @return a Map containing cpu, memory, and requests metrics
     */
    @Operation(
            summary = "Get random metrics",
            description = "Returns random CPU, memory, and request count values for testing purposes."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved random metrics")
    @GetMapping("/metrics")
    public Map<String, Integer> getMetrics() {
        log.debug("traceId={} | GET /metrics called: generating random metrics", MDC.get("traceId"));

        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("cpu", (int)(Math.random() * 100));// Random CPU usage
        metrics.put("memory", (int)(Math.random() * 100));// Random Memory usage
        metrics.put("requests", (int)(Math.random() * 1000));// Random number of requests

        log.info("traceId={} | Generated random metrics: cpu={}, memory={}, requests={}",
                MDC.get("traceId"), metrics.get("cpu"), metrics.get("memory"), metrics.get("requests"));

        return metrics;
    }


    @Operation(
            summary = "View all metrics from DB",
            description = "Retrieves all collected metrics stored in the database as DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stored metrics")
    @GetMapping("/metrics/view")
    public List<MetricDto> viewMetrics() {
        log.debug("traceId={} | GET /metrics/view called: fetching all metrics from database", MDC.get("traceId"));

        try {
            List<MetricDto> metricDtos = metricService.findAll();
            log.info("traceId={} | Retrieved {} metrics from DB", MDC.get("traceId"), metricDtos.size());
            return metricDtos;
        } catch (Exception e) {
            log.error("traceId={} | Error fetching metrics from database: {}", MDC.get("traceId"), e.getMessage(), e);
            throw e;
        }
    }

}
