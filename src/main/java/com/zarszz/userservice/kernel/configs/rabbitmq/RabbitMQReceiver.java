package com.zarszz.userservice.kernel.configs.rabbitmq;

import com.google.gson.Gson;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import com.zarszz.userservice.requests.v1.email.SendSecretCodeFromEmail;
import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;
import com.zarszz.userservice.service.EmailSenderService;
import com.zarszz.userservice.service.NotificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQReceiver implements RabbitListenerConfigurer {

    @Autowired
    NotificationServiceImpl notificationService;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    Gson gson;

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void receivedMessage(Message message) throws Exception {
        switch (message.getPurpose()) {
            case SEND_NOTIFICATION:
                log.info("Purpose : SEND_NOTIFICATION, received message : {}", message.getMessage());
                var notificationMessage = gson.fromJson(message.getMessage(), NotificationMessageDto.class);
                String result = this.notificationService.sendNotificationRestTemplate(notificationMessage);
                if (result.equals(""))
                    throw new Exception("Send notification failed");
                break;
            case SEND_EMAIL:
                log.info("Purpose : SEND_EMAIL, received message : {}", message.getMessage());
                var sendSecretCodeFromEmail = gson.fromJson(message.getMessage(), SendSecretCodeFromEmail.class);
                emailSenderService.sendMailWithInline(
                        sendSecretCodeFromEmail.getRecipientName(), sendSecretCodeFromEmail.getRecipientEmail());
                break;
            case PROCESS_PAYMENT:
                log.info("Purpose : PROCESS_PAYMENT, received message : {}", message.getMessage());
                break;
        }
    }
}
