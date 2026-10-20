package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumStateRequest;

/** Aprobada: final. */
class AcceptedRequestState extends BaseRequestState {
    @Override
    public EnumStateRequest value() {
        return EnumStateRequest.ACCEPT;
    }

    @Override
    protected String label() {
        return "aprobada";
    }
}
