package com.monitoring.monitoring_service.notifier;

import com.monitoring.monitoring_service.model.Alarm;
import org.springframework.stereotype.Component;

/**
 * ConsoleNotifier prints alarms to the console.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Component
public class ConsoleNotifier implements Notifier {

    @Override
    public void notify(Alarm alarm) {
        System.out.println("[ALARM] " + alarm.getSeverity() +
                " | Service: " + alarm.getServiceName() +
                " | Metric: " + alarm.getMetricName() +
                " | Value: " + alarm.getValue() +
                " | Message: " + alarm.getMessage() +
                " | Time: " + alarm.getTimestamp());
    }
}