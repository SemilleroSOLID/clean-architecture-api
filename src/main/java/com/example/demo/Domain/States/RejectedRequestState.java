package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumStateRequest;

/** Rechazada: final; keeps the justification for the student. */
class RejectedRequestState extends BaseRequestState {
    @Override
    public EnumStateRequest value() {
        return EnumStateRequest.REJECT;
    }

    @Override
    protected String label() {
        return "rechazada";
    }
}
