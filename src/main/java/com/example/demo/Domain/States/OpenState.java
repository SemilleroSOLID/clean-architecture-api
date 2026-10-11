package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Exceptions.InvalidConvocationStateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Abierta: initial state; receives requests between its start and end dates. */
class OpenState extends BaseConvocationState {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    @Override
    public void ensureCanReceiveRequests(Convocation convocation, LocalDate today) {
        if (today.isBefore(convocation.getStartDate())) {
            throw new InvalidConvocationStateException("La convocatoria aún no recibe solicitudes: abre el "
                    + convocation.getStartDate().format(DATE_FORMAT));
        }
        if (today.isAfter(convocation.getEndDate())) {
            throw new InvalidConvocationStateException("La convocatoria ya no recibe solicitudes: cerró el "
                    + convocation.getEndDate().format(DATE_FORMAT));
        }
    }
}
