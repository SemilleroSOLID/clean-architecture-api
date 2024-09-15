package com.example.demo.Application.RabbitInterfaces;

public interface IRabbitMQService {
    void init();
    void connect();
    void close();
    void send();
}
