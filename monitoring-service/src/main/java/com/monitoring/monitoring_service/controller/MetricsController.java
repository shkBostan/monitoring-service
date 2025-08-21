package com.monitoring.monitoring_service.controller;

import com.monitoring.monitoring_service.dto.MetricDto;
import com.monitoring.monitoring_service.model.Metric;
import org.springframework.ui.Model;
import com.monitoring.monitoring_service.service.MetricService;
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
@RestController
public class MetricsController {
    @Autowired
    MetricService metricService;


    /**
     * Returns random sample metrics.
     * Endpoint: GET /metrics
     *
     * @return a Map containing cpu, memory, and requests metrics
     */
    @GetMapping("/metrics")
    public Map<String, Integer> getMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("cpu", (int)(Math.random() * 100));// Random CPU usage
        metrics.put("memory", (int)(Math.random() * 100));// Random Memory usage
        metrics.put("requests", (int)(Math.random() * 1000));// Random number of requests
        return metrics;
    }


    @GetMapping("/metrics/view")
    public List<MetricDto> viewMetrics() {
        return metricService.findAll();
    }

}
