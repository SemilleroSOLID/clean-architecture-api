package com.example.demo.Infrastructure.Implementations.Services;

import com.example.demo.Domain.Interfaces.IAMQP;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQService implements IAMQP {

    public static final String QUEUE_NAME = "event.drivent.email";
    public static final String EXCHANGE_NAME = "event.drivent.exchange";
    public static final String ROUTING_KEY = "email";

    @Bean
    @Override
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    @Override
    public Exchange exchange() {
        return new DirectExchange(EXCHANGE_NAME, false, false);
    }

    @Bean
    @Override
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY).noargs();
    }
}
