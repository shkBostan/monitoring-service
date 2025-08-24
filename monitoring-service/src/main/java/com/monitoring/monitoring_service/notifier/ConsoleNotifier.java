package com.monitoring.monitoring_service.notifier;

import com.monitoring.monitoring_service.model.Alarm;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


/**
 * ConsoleNotifier prints alarms to the console.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Slf4j
@Component
public class ConsoleNotifier implements Notifier {

    @Override
    public void notify(Alarm alarm) {
        switch (alarm.getSeverity()) {
            case "CRITICAL" -> log.error("[ALARM] {} | service={} metric={} value={} msg={} time={}",
                    alarm.getSeverity(), alarm.getServiceName(), alarm.getMetricName(), alarm.getValue(),
                    alarm.getMessage(), alarm.getTimestamp());
            case "WARNING" -> log.warn("[ALARM] {} | service={} metric={} value={} msg={} time={}",
                    alarm.getSeverity(), alarm.getServiceName(), alarm.getMetricName(), alarm.getValue(),
                    alarm.getMessage(), alarm.getTimestamp());
            default -> log.info("[ALARM] {} | service={} metric={} value={} msg={} time={}",
                    alarm.getSeverity(), alarm.getServiceName(), alarm.getMetricName(), alarm.getValue(),
                    alarm.getMessage(), alarm.getTimestamp());
        }
    }
}