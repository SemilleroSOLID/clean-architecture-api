package com.example.demo.Application.RabbitInterfaces;

import java.util.List;

public interface IAmqpConfig {
    IAmqpConnection getConnection();
    List<IAmqpExchange> getExchanges();
    List<IAmqpQueue> getQueues();
    List<IAmqpBinding> getBinding();
}
