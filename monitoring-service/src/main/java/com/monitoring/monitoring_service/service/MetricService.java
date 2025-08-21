package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.dto.MetricDto;
import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.repository.MetricRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on Aug, 2025
 *
 * @author s Bostan
 */
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
            System.out.println("MetricDto => id=" + dto.getId() +
                    ", name=" + dto.getName() +
                    ", value=" + dto.getValue());
        }
    }

    /**
     * Get all metrics mapped to DTOs.
     *
     * @return list of MetricDto
     */
    public List<MetricDto> findAll() {
        return metricRepository.findAll()
                .stream()
                .flatMap(metric -> List.of(
                        new MetricDto(metric.getId(), metric.getServiceName() + "_CPU", (double) metric.getCpu()),
                        new MetricDto(metric.getId(), metric.getServiceName() + "_MEMORY", (double) metric.getMemory()),
                        new MetricDto(metric.getId(), metric.getServiceName() + "_REQUESTS", (double) metric.getRequests())
                ).stream())
                .collect(Collectors.toList());
    }
}
