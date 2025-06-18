package com.saminavi.attendancePortal.SaminaviAttendanceApplication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    /**
     * Send email asynchronously to handle concurrent requests
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String text) {
        try {
            logger.info("Sending email to: {} with subject: {}", to, subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            
            logger.info("Email sent successfully to: {}", to);
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            logger.error("Failed to send email to: {} with subject: {}", to, subject, e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Send email synchronously (for backward compatibility)
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            logger.info("Sending email to: {} with subject: {}", to, subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            
            logger.info("Email sent successfully to: {}", to);
            
        } catch (Exception e) {
            logger.error("Failed to send email to: {} with subject: {}", to, subject, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send multiple emails concurrently
     */
    public void sendMultipleEmailsAsync(java.util.List<EmailRequest> emailRequests) {
        logger.info("Sending {} emails concurrently", emailRequests.size());
        
        emailRequests.parallelStream().forEach(emailRequest -> {
            sendEmailAsync(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getText())
                .thenAccept(success -> {
                    if (success) {
                        logger.info("Concurrent email sent successfully to: {}", emailRequest.getTo());
                    } else {
                        logger.error("Failed to send concurrent email to: {}", emailRequest.getTo());
                    }
                });
        });
    }

    /**
     * Email request wrapper class
     */
    public static class EmailRequest {
        private final String to;
        private final String subject;
        private final String text;

        public EmailRequest(String to, String subject, String text) {
            this.to = to;
            this.subject = subject;
            this.text = text;
        }

        public String getTo() { return to; }
        public String getSubject() { return subject; }
        public String getText() { return text; }
    }
} 