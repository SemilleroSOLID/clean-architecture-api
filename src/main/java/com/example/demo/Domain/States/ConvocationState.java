package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Enums.EnumConvocationState;

import java.time.LocalDate;

/**
 * State pattern: each state of a convocation decides which actions it allows and which state comes next
 * (diagram in diagramas/estados-convocatoria.md, issue #30).
 */
public interface ConvocationState {
    EnumConvocationState value();

    /** Closes an open convocation whose end date already passed (automatic closing). */
    void applyDateRules(Convocation convocation, LocalDate today);

    void ensureCanBeEdited(Convocation convocation);

    void ensureCanBeDeleted(Convocation convocation);

    void close(Convocation convocation);

    void reopen(Convocation convocation, LocalDate newEndDate, LocalDate today);

    void finish(Convocation convocation, boolean hasUndecidedRequests);

    void cancel(Convocation convocation, String reason);

    boolean canReceiveRequests(Convocation convocation, LocalDate today);

    boolean canDecideRequests();
}
