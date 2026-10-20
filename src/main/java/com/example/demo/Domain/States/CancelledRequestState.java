package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumStateRequest;

/** Retirada por el estudiante: final, it is no longer considered for the selection. */
class CancelledRequestState extends BaseRequestState {
    @Override
    public EnumStateRequest value() {
        return EnumStateRequest.CANCELLED;
    }

    @Override
    protected String label() {
        return "retirada";
    }
}
