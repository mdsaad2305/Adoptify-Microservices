package com.microservices.adoptify.user_service.messaging;

import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.dto.UserMessageDTO;
import com.microservices.adoptify.user_service.model.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public UserEmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(User user) {
        UserMessageDTO userMessageDTO = new UserMessageDTO();
        userMessageDTO.setEmail(user.getEmail());
        userMessageDTO.setId(user.getUserId());
        userMessageDTO.setUsername(user.getUsername());
        rabbitTemplate.convertAndSend("userNotificationQueue", userMessageDTO);
    }
}
