package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.config.AlarmConfig;
import com.monitoring.monitoring_service.model.Alarm;
import com.monitoring.monitoring_service.model.AlarmEntity;
import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.notifier.Notifier;
import com.monitoring.monitoring_service.repository.AlarmRepository;
import com.monitoring.monitoring_service.repository.MetricRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AlarmService checks collected metrics against configured thresholds,
 * triggers alarms to Notifiers, and stores them in the database.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Service
public class AlarmService {

    private final MetricRepository metricRepository;
    private final AlarmConfig alarmConfig;
    private final List<Notifier> notifiers;
    private final AlarmRepository alarmRepository;

    // To prevent alarm spamming (cooldown between alarms)
    private LocalDateTime lastAlarmTime = LocalDateTime.MIN;

    public AlarmService(MetricRepository metricRepository,
                        AlarmConfig alarmConfig,
                        List<Notifier> notifiers,
                        AlarmRepository alarmRepository) {
        this.metricRepository = metricRepository;
        this.alarmConfig = alarmConfig;
        this.notifiers = notifiers;
        this.alarmRepository = alarmRepository;
    }

    /**
     * Periodically fetches the latest metrics and evaluates them against thresholds.
     * If a threshold is exceeded, an Alarm is created, sent to Notifiers,
     * and stored in the database.
     */
    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void checkMetricsForAlarms() {
        List<Metric> metrics = metricRepository.findAll();
        if (metrics.isEmpty()) {
            return;
        }

        Metric latest = metrics.get(metrics.size() - 1); // latest metric entry

        checkThreshold(latest.getServiceName(), "CPU", latest.getCpu(),
                alarmConfig.getCpuThresholdWarning(), alarmConfig.getCpuThresholdCritical());

        checkThreshold(latest.getServiceName(), "MEMORY", latest.getMemory(),
                alarmConfig.getMemoryThresholdWarning(), alarmConfig.getMemoryThresholdCritical());

        checkThreshold(latest.getServiceName(), "REQUESTS", latest.getRequests(),
                alarmConfig.getRequestsThresholdWarning(), alarmConfig.getRequestsThresholdCritical());
    }

    private void checkThreshold(String serviceName, String metricName, double value,
                                int warningThreshold, int criticalThreshold) {
        String severity = null;

        if (value >= criticalThreshold) {
            severity = "CRITICAL";
        } else if (value >= warningThreshold) {
            severity = "WARNING";
        }

        if (severity != null) {
            // Respect cooldown to avoid spamming
            if (lastAlarmTime.plusSeconds(alarmConfig.getCooldownSeconds()).isAfter(LocalDateTime.now())) {
                return;
            }

            // Create runtime Alarm for Notifiers
            Alarm alarm = new Alarm(
                    serviceName,
                    metricName,
                    value,
                    severity,
                    LocalDateTime.now(),
                    String.format("%s level reached for %s: %.2f", severity, metricName, value)
            );

            // Send to all Notifiers
            for (Notifier notifier : notifiers) {
                notifier.notify(alarm);
            }

            // Save to database as AlarmEntity
            AlarmEntity entity = new AlarmEntity();
            entity.setServiceName(alarm.getServiceName());
            entity.setMetricName(alarm.getMetricName());
            entity.setMetricValue(alarm.getValue());
            entity.setSeverity(alarm.getSeverity());
            entity.setTimestamp(alarm.getTimestamp());
            entity.setMessage(alarm.getMessage());

            alarmRepository.save(entity);

            lastAlarmTime = LocalDateTime.now();
        }
    }
}