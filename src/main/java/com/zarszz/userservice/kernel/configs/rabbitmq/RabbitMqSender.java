package com.zarszz.userservice.kernel.configs.rabbitmq;

import com.zarszz.userservice.domain.User;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingkey}")
    private String routingKey;

    public void send(Message message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
