package com.example.demo.Application.RabbitInterfaces;

public interface IAmqpBinding {
    String getQueue();
    String getExchange();
    String getKey();
}

