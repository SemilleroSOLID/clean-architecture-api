package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import com.example.demo.Domain.Exceptions.InvalidConvocationStateException;

import java.time.LocalDate;
import java.util.Map;

/** By default a state allows nothing; each concrete state overrides only the actions it allows. */
abstract class BaseConvocationState implements ConvocationState {

    /** Name of the state as it is shown to the user, for example "cerrada". */
    protected abstract String label();

    protected InvalidConvocationStateException notAllowed(String action) {
        return new InvalidConvocationStateException("No se puede " + action + " una convocatoria " + label());
    }

    @Override
    public void applyDateRules(Convocation convocation, LocalDate today) {
        // Only an open convocation changes state with the dates.
    }

    @Override
    public void ensureCanBeEdited(Convocation convocation) {
        throw notAllowed("editar");
    }

    @Override
    public void ensureCanBeDeleted(Convocation convocation) {
        throw notAllowed("eliminar");
    }

    @Override
    public void close(Convocation convocation) {
        throw notAllowed("cerrar");
    }

    @Override
    public void reopen(Convocation convocation, LocalDate newEndDate, LocalDate today) {
        throw notAllowed("reabrir");
    }

    @Override
    public void finish(Convocation convocation, boolean hasUndecidedRequests) {
        throw notAllowed("finalizar");
    }

    @Override
    public void cancel(Convocation convocation, String reason) {
        throw notAllowed("cancelar");
    }

    @Override
    public boolean canReceiveRequests(Convocation convocation, LocalDate today) {
        return false;
    }

    @Override
    public boolean canDecideRequests() {
        return false;
    }

    /** Cancelling is allowed in more than one state and always needs a reason. */
    protected void cancelWithReason(Convocation convocation, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new DomainValidationException(Map.of("reason", "La justificación de la cancelación es obligatoria"));
        }
        convocation.setCancellationReason(reason.trim());
        convocation.setState(EnumConvocationState.CANCELLED);
    }
}
