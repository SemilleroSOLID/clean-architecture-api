package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumConvocationState;

/** Finalizada: every request was decided; read only. */
class FinishedState extends BaseConvocationState {

    @Override
    public EnumConvocationState value() {
        return EnumConvocationState.FINISHED;
    }

    @Override
    protected String label() {
        return "finalizada";
    }
}
