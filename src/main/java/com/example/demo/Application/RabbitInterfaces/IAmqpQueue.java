package com.example.demo.Application.RabbitInterfaces;

import java.util.Map;

public interface IAmqpQueue {
    String getName();
    Map<String, Object> getOptions();
}

