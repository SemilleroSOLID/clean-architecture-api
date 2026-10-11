package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumConvocationState;

import java.util.Map;

/** Gives the state object for the state stored in the convocation. The states hold no data, so one instance each is enough. */
public final class ConvocationStates {
    private static final Map<EnumConvocationState, ConvocationState> STATES = Map.of(
            EnumConvocationState.OPEN, new OpenState(),
            EnumConvocationState.CLOSED, new ClosedState(),
            EnumConvocationState.FINISHED, new FinishedState(),
            EnumConvocationState.CANCELLED, new CancelledState());

    private ConvocationStates() {}

    public static ConvocationState of(EnumConvocationState state) {
        return STATES.get(state);
    }
}
