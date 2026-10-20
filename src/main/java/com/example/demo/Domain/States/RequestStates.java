package com.example.demo.Domain.States;

import com.example.demo.Domain.Enums.EnumStateRequest;

import java.util.Map;

/** Gives the state object for the state stored in the request. The states hold no data, so one instance each is enough. */
public final class RequestStates {
    private static final Map<EnumStateRequest, RequestState> STATES = Map.of(
            EnumStateRequest.PENDING, new ReviewRequestState(EnumStateRequest.PENDING),
            EnumStateRequest.ACTIVE, new ReviewRequestState(EnumStateRequest.ACTIVE),
            EnumStateRequest.ACCEPT, new AcceptedRequestState(),
            EnumStateRequest.REJECT, new RejectedRequestState(),
            EnumStateRequest.CANCELLED, new CancelledRequestState());

    private RequestStates() {}

    public static RequestState of(EnumStateRequest state) {
        return STATES.get(state);
    }
}
