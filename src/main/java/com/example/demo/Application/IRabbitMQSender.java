package com.example.demo.Application;

import com.example.demo.Application.Events.RequestStatusChangedEvent;

/**
 * Publishes the events of the application to the notification service (issue #20). The implementation lives in
 * Infrastructure; it never makes the business operation fail if the broker is not available.
 */
public interface IRabbitMQSender {
    void publishRequestStatusChanged(RequestStatusChangedEvent event);
}
