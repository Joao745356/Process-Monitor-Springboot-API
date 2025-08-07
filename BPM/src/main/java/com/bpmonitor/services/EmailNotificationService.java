package com.bpmonitor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService  {

	
	 private final JavaMailSender mailSender;

	    @Value("${spring.mail.username}")
	    private String fromEmail;

	    public EmailNotificationService(JavaMailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	    @Override
	    public void notifyResponsibleParty(String email, String message) {
	        try {
	            SimpleMailMessage mailMessage = new SimpleMailMessage();
	            mailMessage.setTo(email);
	            mailMessage.setFrom(fromEmail);
	            mailMessage.setSubject(" Task Error Notification ! ");
	            mailMessage.setText(message);

	            mailSender.send(mailMessage);
	        } catch (Exception e) {
	            // Log failure, fallback, retry logic, etc.
	            System.err.println("Failed to send notification to " + email);
	            e.printStackTrace();
	        }
	    }
}
