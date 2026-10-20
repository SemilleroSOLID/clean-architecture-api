package com.example.demo.Application.Events;

import com.example.demo.Domain.Enums.EnumStateRequest;

import java.io.Serializable;

/**
 * Message published to RabbitMQ every time a request is created or changes its state (issue #20), so the
 * notification service can tell the student. It is the contract with the consumer: add fields, do not rename them.
 * previousState is null when the request was just created.
 */
public class RequestStatusChangedEvent implements Serializable {
    public static final String EVENT_NAME = "REQUEST_STATUS_CHANGED";

    private String event = EVENT_NAME;
    private int requestId;
    private int studentId;
    private String studentName;
    private String studentEmail;
    private int convocationId;
    private String convocationTitle;
    private EnumStateRequest previousState;
    private EnumStateRequest newState;
    private String comment;
    private String date;

    public RequestStatusChangedEvent() {}

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(int convocationId) {
        this.convocationId = convocationId;
    }

    public String getConvocationTitle() {
        return convocationTitle;
    }

    public void setConvocationTitle(String convocationTitle) {
        this.convocationTitle = convocationTitle;
    }

    public EnumStateRequest getPreviousState() {
        return previousState;
    }

    public void setPreviousState(EnumStateRequest previousState) {
        this.previousState = previousState;
    }

    public EnumStateRequest getNewState() {
        return newState;
    }

    public void setNewState(EnumStateRequest newState) {
        this.newState = newState;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return event + " request " + requestId + ": " + previousState + " -> " + newState;
    }
}
