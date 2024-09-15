package com.example.demo.Application.RabbitClasses;

import com.example.demo.Application.RabbitInterfaces.IAmqpQueue;

import java.util.Map;

public class AmqpQueue implements IAmqpQueue {
    private final String name;
    private final Map<String, Object> options;

    public AmqpQueue(String name, Map<String, Object> options) {
        this.name = name;
        this.options = options;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getOptions() {
        return options;
    }
}

