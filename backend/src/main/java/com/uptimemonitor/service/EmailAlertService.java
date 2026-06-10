package com.uptimemonitor.service;

import com.uptimemonitor.entity.AlertType;
import com.uptimemonitor.entity.Monitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailAlertService {

    private final JavaMailSender mailSender;

    public EmailAlertService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlert(Monitor monitor, AlertType alertType, Integer statusCode, long responseTimeMs) {
        
        String subject = alertType == AlertType.DOWN
                ? "Monitor Down: " + monitor.getName()
                : "Monitor Recovered: " + monitor.getName();

        String body = """
                Hello,

                Your monitor "%s" changed status.

                URL: %s
                Alert type: %s
                HTTP status code: %s
                Response time: %d ms

                Regards,
                Uptime Monitor
                """.formatted(
                monitor.getName(),
                monitor.getUrl(),
                alertType,
                statusCode != null ? statusCode : "N/A",
                responseTimeMs
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(monitor.getUser().getEmail());
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            log.error("Failed to send {} alert for monitor {}", alertType, monitor.getId(), exception);
            throw exception;
        }
    }
}
