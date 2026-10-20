package com.example.demo.Infrastructure.Implementations.Services;

import com.example.demo.Application.Events.RequestStatusChangedEvent;
import com.example.demo.Application.IRabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RabbitMQSender implements IRabbitMQSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public RabbitMQSender() {}

    RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Inside a transaction the event leaves only once the change is saved, so the student is never told about a rollback. */
    @Override
    public void publishRequestStatusChanged(RequestStatusChangedEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(event);
                }
            });
        } else {
            send(event);
        }
    }

    // The state change is already saved: if RabbitMQ is down only the notification is lost, and it is logged.
    private void send(RequestStatusChangedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQService.EXCHANGE_NAME, RabbitMQService.ROUTING_KEY, event);
            LOGGER.info("Evento publicado en RabbitMQ: {}", event);
        } catch (AmqpException exception) {
            LOGGER.error("No se pudo publicar en RabbitMQ el evento {}: {}", event, exception.getMessage());
        }
    }
}
