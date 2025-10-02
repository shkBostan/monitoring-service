package com.monitoring.monitoring_service.notifier;

import com.monitoring.monitoring_service.model.Alarm;

/**
 * Notifier interface for sending alarms to different channels.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
public interface Notifier {
    void notify(Alarm alarm);
}
