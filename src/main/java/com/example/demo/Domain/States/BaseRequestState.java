package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Exceptions.InvalidRequestStateException;

/** By default a request state allows nothing; only the state in review overrides the actions. */
abstract class BaseRequestState implements RequestState {

    /** Name of the state as it is shown to the user, for example "rechazada". */
    protected abstract String label();

    protected InvalidRequestStateException notAllowed(String action) {
        return new InvalidRequestStateException("No se puede " + action + " una solicitud " + label());
    }

    @Override
    public void approve(Request request, Convocation convocation) {
        throw notAllowed("aprobar");
    }

    @Override
    public void reject(Request request, Convocation convocation, String comment) {
        throw notAllowed("rechazar");
    }

    @Override
    public void cancel(Request request) {
        throw notAllowed("retirar");
    }
}
