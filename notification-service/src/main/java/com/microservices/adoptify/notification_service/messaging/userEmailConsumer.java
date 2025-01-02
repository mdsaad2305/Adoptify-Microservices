package com.microservices.adoptify.notification_service.messaging;

import com.microservices.adoptify.notification_service.dto.UserMessageDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class userEmailConsumer {

    @RabbitListener(queues = "userNotificationQueue")
    public void handleUserRegisteredEmail(UserMessageDTO userMessageDTO) {
        sendWelcomeEmail(userMessageDTO.getEmail(), userMessageDTO.getId(), userMessageDTO.getUsername());
    }

    public void sendWelcomeEmail(String email, Long userId, String username) {
        System.out.println("Sending welcome email to " + email + " for user " + username);
    }

}
