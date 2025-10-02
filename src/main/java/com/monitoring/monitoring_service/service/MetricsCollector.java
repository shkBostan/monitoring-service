package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.config.MonitoringConfig;
import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.repository.MetricRepository;

import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

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
@Slf4j
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


    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;



    @EventListener(ApplicationReadyEvent.class)
    public void collectMetricsAfterStartup() {
        log.info("Application fully started, executing initial metrics collection...");
        collectMetrics(); // first save execution
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

            //  (Basic Auth)
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(username, password);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> metrics = response.getBody();
            if (metrics != null) {
                Metric metric = new Metric();
                metric.setServiceName(config.getServiceName());
                metric.setCpu((Integer) metrics.get("cpu"));
                metric.setMemory((Integer) metrics.get("memory"));
                metric.setRequests((Integer) metrics.get("requests"));
                metric.setTimestamp(LocalDateTime.now());

                repository.save(metric);
                log.info("Collected metrics service={} cpu={} memory={} requests={}",
                        metric.getServiceName(), metric.getCpu(), metric.getMemory(), metric.getRequests());
            }else {
                log.warn("Metrics endpoint returned null payload.");
            }
        } catch (Exception e) {
            log.error("Failed to collect metrics from /metrics " , e);
        }
    }

}