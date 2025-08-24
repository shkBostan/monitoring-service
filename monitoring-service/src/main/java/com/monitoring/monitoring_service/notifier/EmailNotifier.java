package com.monitoring.monitoring_service.notifier;

import com.monitoring.monitoring_service.model.Alarm;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * EmailNotifier sends alarms via email.
 *
 * @since Aug, 2025
 */
@Component
public class EmailNotifier implements Notifier {

    private final JavaMailSender mailSender;

    public EmailNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void notify(Alarm alarm) {
        System.out.println("SendMail..." );
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
        }catch (Exception e){
            System.err.println(e);
        }
        System.out.println("Email sent for alarm: " + alarm.getMessage());
    }

}
