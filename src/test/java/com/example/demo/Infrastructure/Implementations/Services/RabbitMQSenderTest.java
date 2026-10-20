package com.example.demo.Infrastructure.Implementations.Services;

import com.example.demo.Application.Events.RequestStatusChangedEvent;
import com.example.demo.Domain.Enums.EnumStateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class RabbitMQSenderTest {
    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final RabbitMQSender sender = new RabbitMQSender(rabbitTemplate);

    private static RequestStatusChangedEvent event() {
        RequestStatusChangedEvent event = new RequestStatusChangedEvent();
        event.setRequestId(1);
        event.setPreviousState(EnumStateRequest.PENDING);
        event.setNewState(EnumStateRequest.ACCEPT);
        return event;
    }

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void publishesTheEventToTheExchangeOfTheNotifications() {
        RequestStatusChangedEvent event = event();
        sender.publishRequestStatusChanged(event);
        verify(rabbitTemplate).convertAndSend(RabbitMQService.EXCHANGE_NAME, RabbitMQService.ROUTING_KEY, event);
    }

    @Test
    void withRabbitMQDownTheErrorIsLoggedAndTheOperationDoesNotFail() {
        doThrow(new AmqpConnectException(new ConnectException("Connection refused")))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
        assertDoesNotThrow(() -> sender.publishRequestStatusChanged(event()));
    }

    @Test
    void insideATransactionTheEventLeavesOnlyAfterTheCommit() {
        TransactionSynchronizationManager.initSynchronization();
        RequestStatusChangedEvent event = event();
        sender.publishRequestStatusChanged(event);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(rabbitTemplate).convertAndSend(RabbitMQService.EXCHANGE_NAME, RabbitMQService.ROUTING_KEY, event);
    }
}
