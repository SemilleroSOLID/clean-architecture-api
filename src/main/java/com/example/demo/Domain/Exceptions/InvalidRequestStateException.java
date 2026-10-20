package com.example.demo.Domain.Exceptions;

import java.util.Map;

/** An action that the current state of the request does not allow. */
public class InvalidRequestStateException extends DomainValidationException {
    public InvalidRequestStateException(String message) {
        super(Map.of("state", message));
    }
}
