package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumConvocationState;

/** Cancelada: suspended with a reason; read only. */
class CancelledState extends BaseConvocationState {

    @Override
    public EnumConvocationState value() {
        return EnumConvocationState.CANCELLED;
    }

    @Override
    protected String label() {
        return "cancelada";
    }
}
