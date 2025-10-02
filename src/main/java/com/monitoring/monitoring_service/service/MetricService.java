package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.dto.MetricDto;
import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.repository.MetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on Aug, 2025
 *
 * @author s Bostan
 */
@Slf4j
@Service
public class MetricService {
    private final MetricRepository metricRepository;

    public MetricService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    /**
     * Print all metrics as DTOs in the console.
     */
    public void printMetrics() {
        List<MetricDto> metricDtos = findAll();
        for (MetricDto dto : metricDtos) {
            log.info("MetricDto id={} name={} value={}" , dto.getId(), dto.getName(), dto.getValue());
        }
    }

    /**
     * Get all metrics mapped to DTOs.
     *
     * @return list of MetricDto
     */
    public List<MetricDto> findAll() {
        log.debug("Attempting to fetch all metrics from repository...");

        List<MetricDto> result;
        try {

            // Fetch all metrics from repository
            List<Metric> metrics = metricRepository.findAll();
            log.info("Fetched {} metrics from repository.", metrics.size());

            // Transform each Metric into multiple MetricDto objects
            result = metrics.stream()
                    .flatMap(metric -> {
                        log.debug("Processing metric id={} service={}", metric.getId(), metric.getServiceName());
                        return List.of(
                                new MetricDto(metric.getId(), metric.getServiceName() + "_CPU", (double) metric.getCpu()),
                                new MetricDto(metric.getId(), metric.getServiceName() + "_MEMORY", (double) metric.getMemory()),
                                new MetricDto(metric.getId(), metric.getServiceName() + "_REQUESTS", (double) metric.getRequests())
                        ).stream();
                    })
                    .collect(Collectors.toList());

            log.info("Successfully transformed {} metrics into DTOs.", result.size());

        } catch (Exception e) {
            log.error("Error occurred while fetching metrics: {}", e.getMessage(), e);
            throw e; //Rethrow the exception so it can be handled at a higher level
        }

        return result; // Return the result list of DTOs
    }
}
