package com.example.demo.Application;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public interface IRabbitMQSender {
    String sendMessage(String message);

}
