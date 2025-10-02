package com.monitoring.monitoring_service.notifier;

import com.monitoring.monitoring_service.model.Alarm;
import org.slf4j.MDC;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * EmailNotifier sends alarms via email.
 *
 * @since Aug, 2025
 */
@Slf4j
@Component
public class EmailNotifier implements Notifier {

    private final JavaMailSender mailSender;

    public EmailNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void notify(Alarm alarm) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("s@gmail.com"); //Receiver email
            message.setSubject("[ALARM] " + alarm.getSeverity() + " - " + alarm.getServiceName());
            message.setText(
                    "Service: " + alarm.getServiceName() + "\n" +
                            "Metric: " + alarm.getMetricName() + "\n" +
                            "Value: " + alarm.getValue() + "\n" +
                            "Message: " + alarm.getMessage() + "\n" +
                            "Time: " + alarm.getTimestamp()
            );

            mailSender.send(message);

            log.info("Email sent for alarm severity={} service={} metric={} value={}",
                    alarm.getSeverity(), alarm.getServiceName(), alarm.getMetricName(), alarm.getValue());

        } catch (Exception e) {
            log.error("Email send failed for alarm service={} metric={}", alarm.getServiceName(), alarm.getMetricName(), e);
        }
    }

}
