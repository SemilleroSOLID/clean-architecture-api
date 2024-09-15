package com.example.demo.Application.RabbitClasses;

import com.example.demo.Application.RabbitInterfaces.IAmqpExchange;

import java.util.Map;

public class AmqpExchange implements IAmqpExchange {
    private final String name;
    private final String type;
    private final Map<String, Object> options;

    public AmqpExchange(String name, String type, Map<String, Object> options) {
        this.name = name;
        this.type = type;
        this.options = options;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Map<String, Object> getOptions() {
        return options;
    }
}

