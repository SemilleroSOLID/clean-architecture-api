package com.example.demo.Application.RabbitClasses;

import com.example.demo.Application.RabbitInterfaces.IAmqpBinding;

public class AmqpBinding implements IAmqpBinding {
    private final String queue;
    private final String exchange;
    private final String key;

    public AmqpBinding(String exchange, String queue, String key) {
        this.exchange = exchange;
        this.queue = queue;
        this.key = key;
    }

    @Override
    public String getQueue() {
        return queue;
    }

    @Override
    public String getExchange() {
        return exchange;
    }

    @Override
    public String getKey() {
        return key;
    }
}
