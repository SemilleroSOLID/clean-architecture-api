package com.example.demo.Application.RabbitInterfaces;

import java.util.Map;

public interface IAmqpExchange {
    String getName();
    String getType();
    Map<String, Object> getOptions();
}
