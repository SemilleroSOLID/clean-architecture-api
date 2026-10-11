package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import com.example.demo.Domain.Exceptions.InvalidConvocationStateException;

import java.time.LocalDate;
import java.util.Map;

/** Cerrada: no new requests; the administrator decides the received ones. */
class ClosedState extends BaseConvocationState {

    @Override
    public EnumConvocationState value() {
        return EnumConvocationState.CLOSED;
    }

    @Override
    protected String label() {
        return "cerrada";
    }

    @Override
    public void reopen(Convocation convocation, LocalDate newEndDate, LocalDate today) {
        if (newEndDate == null || !newEndDate.isAfter(today)) {
            throw new DomainValidationException(Map.of("endDate", "La nueva fecha de fin debe ser posterior a la fecha actual"));
        }
        if (newEndDate.isBefore(convocation.getStartDate().plusDays(1))) {
            throw new DomainValidationException(Map.of("endDate", "La fecha de fin debe ser al menos un día después de la fecha de inicio"));
        }
        convocation.setEndDate(newEndDate);
        convocation.setState(EnumConvocationState.OPEN);
    }

    @Override
    public void finish(Convocation convocation, boolean hasUndecidedRequests) {
        if (hasUndecidedRequests) {
            throw new InvalidConvocationStateException("No se puede finalizar: hay solicitudes sin aprobar ni rechazar");
        }
        convocation.setState(EnumConvocationState.FINISHED);
    }

    @Override
    public void cancel(Convocation convocation, String reason) {
        cancelWithReason(convocation, reason);
    }

    @Override
    public boolean canDecideRequests() {
        return true;
    }
}
