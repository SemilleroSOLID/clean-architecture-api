package com.example.demo.Infrastructure.Implementations.Services;

import com.example.demo.Application.IRabbitMQSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender implements IRabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQService.EXCHANGE_NAME, RabbitMQService.ROUTING_KEY, message);
        return message;
    }
}
