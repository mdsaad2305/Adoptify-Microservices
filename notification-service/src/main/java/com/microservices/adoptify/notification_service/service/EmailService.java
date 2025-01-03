package com.microservices.adoptify.notification_service.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender javaMailSender;

  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  public void sendEmail(String toEmail, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("Adoptify");
    message.setTo(toEmail);
    message.setSubject(subject);
    message.setText(body);

    try {
      javaMailSender.send(message);
      System.out.println("Mail sent successfully!");
    } catch (MailException e) {
      System.err.println("Error sending mail: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
