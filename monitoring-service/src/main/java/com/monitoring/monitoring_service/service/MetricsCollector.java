package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.config.MonitoringConfig;
import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.repository.MetricRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MetricsCollector is responsible for periodically collecting metrics
 * from the internal /metrics endpoint and saving them to the database.
 *
 * Uses Spring's @Scheduled annotation to run every 5 seconds.
 * Data is stored in H2 in-memory database for MVP testing.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Service
public class MetricsCollector {

    private final MetricRepository repository;
    private final RestTemplate restTemplate;
    private final MonitoringConfig config;


    /**
     * Constructor injection for MetricRepository.
     * Initializes RestTemplate for HTTP requests.
     *
     * @param repository MetricRepository instance
     */
    public MetricsCollector(MetricRepository repository, MonitoringConfig config) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.config = config;
    }

    /**
     * Collects metrics from internal /metrics endpoint every 5 seconds.
     * Saves collected data into H2 database.
     * If endpoint is unavailable, logs the error without crashing.
     */
    @Scheduled(fixedRateString = "#{@monitoringConfig.metricsCollectIntervalMs}")
    public void collectMetrics() {
        try {
            String url = "http://localhost:8080/metrics";
            Map<String, Object> metrics = restTemplate.getForObject(url, Map.class);

            if (metrics != null) {
                Metric metric = new Metric();
                metric.setServiceName(config.getServiceName());
                metric.setCpu((Integer) metrics.get("cpu"));
                metric.setMemory((Integer) metrics.get("memory"));
                metric.setRequests((Integer) metrics.get("requests"));
                metric.setTimestamp(LocalDateTime.now());

                repository.save(metric);

                System.out.println("Collected metrics: " + metric);
            }
        } catch (Exception e) {
            System.err.println("Failed to collect metrics: " + e.getMessage());
        }
    }

}