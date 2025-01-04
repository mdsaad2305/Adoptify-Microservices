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
        String emailBody = """
                <!DOCTYPE html>
                <html>
                  <head>
                    <style>
                      body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f9;
                      }
                      .email-container {
                        max-width: 600px;
                        margin: 20px auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                      }
                      .header {
                        text-align: center;
                        padding: 10px 0;
                        background-color: #1e90ff;
                        color: white;
                        border-radius: 8px 8px 0 0;
                      }
                      .content {
                        padding: 20px;
                        line-height: 1.6;
                        color: #333;
                      }
                      .button {
                        display: block;
                        width: fit-content;
                        margin: 20px auto;
                        padding: 10px 20px;
                        background-color: #1e90ff;
                        color: white;
                        text-align: center;
                        text-decoration: none;
                        border-radius: 4px;
                      }
                      .footer {
                        text-align: center;
                        padding: 10px 0;
                        font-size: 12px;
                        color: #777;
                        border-top: 1px solid #eee;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="email-container">
                      <div class="header">
                        <h2>Welcome to [Your App Name], {{username}}!</h2>
                      </div>
                      <div class="content">
                        <p>Hi {{username}},</p>
                        <p>
                          We're excited to have you on board! You’ve taken the first step toward a better experience with our platform. Our goal is to make sure you have the best tools at your fingertips.
                        </p>
                        <p>
                          To get started, click the button below to explore your dashboard and make the most of your experience.
                        </p>
                        <a href="[insert-dashboard-link-here]" class="button">Go to Dashboard</a>
                        <p>
                          If you have any questions or need assistance, feel free to reach out to our support team.
                        </p>
                        <p>Welcome once again! Let’s make great things happen together!</p>
                        <p>Cheers, <br /> The [Your App Name] Team</p>
                      </div>
                      <div class="footer">
                        <p>
                          You’re receiving this email because you signed up for [Your App Name]. If
                          you didn’t sign up, you can safely ignore this email.
                        </p>
                      </div>
                    </div>
                  </body>
                </html>
                """;
        emailService.sendEmail(email, "Another test", emailBody);
  }
}
