package com.example.demo.Domain.Interfaces;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

public interface IAMQP {
    Queue queue();

    Exchange exchange();

    Binding binding(Queue queue, Exchange exchange);

}
