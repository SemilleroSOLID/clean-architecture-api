package com.example.demo.Application.Exceptions;

/** The user cannot act on a resource of someone else, for example withdraw the request of another student. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
