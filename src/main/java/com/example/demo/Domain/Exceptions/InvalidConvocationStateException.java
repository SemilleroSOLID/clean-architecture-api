package com.example.demo.Domain.Exceptions;

import java.util.Map;

/** An action that the current state of the convocation does not allow. */
public class InvalidConvocationStateException extends DomainValidationException {
    public InvalidConvocationStateException(String message) {
        super(Map.of("state", message));
    }
}
