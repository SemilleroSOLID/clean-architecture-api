package com.example.demo.Application.RabbitClasses;

import com.example.demo.Application.RabbitInterfaces.IAmqpConnection;

public class AmqpConnection implements IAmqpConnection {
    private final String amqpUrl;
    private final int timeout;

    public AmqpConnection(String amqpUrl, int timeout) {
        this.amqpUrl = amqpUrl;
        this.timeout = timeout;
    }

    @Override
    public String getAmqpUrl() {
        return amqpUrl;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }
}

