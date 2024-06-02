package com.example.demo.Application.RabbitInterfaces;

public interface IAmqpConnection {
    String getAmqpUrl();
    int getTimeout();
}
