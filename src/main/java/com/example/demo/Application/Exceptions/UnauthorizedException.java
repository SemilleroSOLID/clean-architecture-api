package com.example.demo.Application.Exceptions;

/** Wrong credentials: answered with 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
