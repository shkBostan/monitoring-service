package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.repository.MetricRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    // print metrics in console
    public void printMetrics() {
        List<Metric> metrics = metricRepository.findAll();
        for (Metric metric : metrics) {
            System.out.println(metric);
        }
    }

    // new method: get all metrics as list
    public List<Metric> findAll() {
        return metricRepository.findAll();
    }
}
