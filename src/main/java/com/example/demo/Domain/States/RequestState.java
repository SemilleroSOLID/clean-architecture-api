package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Enums.EnumStateRequest;

/**
 * State pattern for requests (issue #19), like the one of the convocation (#31): each state decides which
 * actions it allows. Diagram in diagramas/estados-convocatoria.md, annex.
 */
public interface RequestState {
    EnumStateRequest value();

    /** The administrator approves it; the convocation must allow deciding its requests. */
    void approve(Request request, Convocation convocation);

    /** The administrator rejects it with a justification the student will read. */
    void reject(Request request, Convocation convocation, String comment);

    /** The student withdraws it; it is no longer considered for the selection. */
    void cancel(Request request);
}
