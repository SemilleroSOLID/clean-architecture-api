package com.example.demo.Domain.Exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

public class DomainValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public DomainValidationException(Map<String, String> errors) {
        super(String.join("; ", errors.values()));
        this.errors = new LinkedHashMap<>(errors);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
