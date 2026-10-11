package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Enums.EnumConvocationState;

import java.time.LocalDate;

/** Abierta: initial state; receives requests between its start and end dates. */
class OpenState extends BaseConvocationState {

    @Override
    public EnumConvocationState value() {
        return EnumConvocationState.OPEN;
    }

    @Override
    protected String label() {
        return "abierta";
    }

    @Override
    public void applyDateRules(Convocation convocation, LocalDate today) {
        if (today.isAfter(convocation.getEndDate())) {
            convocation.setState(EnumConvocationState.CLOSED);
        }
    }

    @Override
    public void ensureCanBeEdited(Convocation convocation) {
        // Allowed.
    }

    @Override
    public void ensureCanBeDeleted(Convocation convocation) {
        // Allowed; the service also checks that it has no requests (issue #10).
    }

    @Override
    public void close(Convocation convocation) {
        convocation.setState(EnumConvocationState.CLOSED);
    }

    @Override
    public void cancel(Convocation convocation, String reason) {
        cancelWithReason(convocation, reason);
    }

    @Override
    public boolean canReceiveRequests(Convocation convocation, LocalDate today) {
        return !today.isBefore(convocation.getStartDate()) && !today.isAfter(convocation.getEndDate());
    }
}
