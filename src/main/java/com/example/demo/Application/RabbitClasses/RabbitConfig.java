package com.example.demo.Application.RabbitClasses;

import com.example.demo.Application.Constants.RabbitConstants;
import com.example.demo.Application.RabbitInterfaces.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class RabbitConfig implements IAmqpConfig {

    @Value("amqp://admin:admin@localhost:5673/")
    private String amqpUrl;

    @Override
    public IAmqpConnection getConnection() {
        return new AmqpConnection(amqpUrl, 2000);
    }

    @Override
    public List<IAmqpExchange> getExchanges() {
        return List.of(new AmqpExchange(
                RabbitConstants.EXCHANGE_NAME,
                "direct",
                Map.of("publishTimeout", 1000, "persistent", true, "durable", false)
        ));
    }

    @Override
    public List<IAmqpQueue> getQueues() {
        return List.of(
                new AmqpQueue(RabbitConstants.WHATSAPP_QUEUE, Map.of("limit", 1000, "queueLimit", 1000)),
                new AmqpQueue(RabbitConstants.EMAIL_QUEUE, Map.of("limit", 1000, "queueLimit", 1000)),
                new AmqpQueue(RabbitConstants.SMS_QUEUE, Map.of("limit", 1000, "queueLimit", 1000))
        );
    }

    @Override
    public List<IAmqpBinding> getBinding() {
        return List.of(
                new AmqpBinding(RabbitConstants.EXCHANGE_NAME, RabbitConstants.WHATSAPP_QUEUE, RabbitConstants.WHATSAPP_KEY),
                new AmqpBinding(RabbitConstants.EXCHANGE_NAME, RabbitConstants.EMAIL_QUEUE, RabbitConstants.EMAIL_KEY),
                new AmqpBinding(RabbitConstants.EXCHANGE_NAME, RabbitConstants.SMS_QUEUE, RabbitConstants.SMS_KEY)
        );
    }
}

