package com.microservices.adoptify.notification_service.messaging;

import com.microservices.adoptify.notification_service.dto.UserMessageDTO;
import com.microservices.adoptify.notification_service.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class userEmailConsumer {

  private final EmailService emailService;

  public userEmailConsumer(EmailService emailService) {
    this.emailService = emailService;
  }

  @RabbitListener(queues = "userNotificationQueue")
  public void handleUserRegisteredEmail(UserMessageDTO userMessageDTO) {
    sendWelcomeEmail(
        userMessageDTO.getEmail(), userMessageDTO.getId(), userMessageDTO.getUsername());
  }

  public void sendWelcomeEmail(String email, Long userId, String username) {
    System.out.println("Sending welcome email to " + email + " for user " + username); // LOG
    emailService.sendEmail(email, "Another test", "This is a test email.");
  }
}
