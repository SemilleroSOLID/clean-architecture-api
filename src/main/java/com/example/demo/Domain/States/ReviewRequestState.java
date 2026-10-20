package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Exceptions.DomainValidationException;

import java.util.Map;

/**
 * En revisión: PENDING (does not meet some requirement) and ACTIVE (meets all) wait for the administrator and
 * behave the same; the difference is only what the administrator sees (issue #14).
 */
class ReviewRequestState extends BaseRequestState {
    private final EnumStateRequest value;

    ReviewRequestState(EnumStateRequest value) {
        this.value = value;
    }

    @Override
    public EnumStateRequest value() {
        return value;
    }

    @Override
    protected String label() {
        return "en revisión";
    }

    @Override
    public void approve(Request request, Convocation convocation) {
        convocation.ensureCanDecideRequests();
        request.setState(EnumStateRequest.ACCEPT);
    }

    @Override
    public void reject(Request request, Convocation convocation, String comment) {
        convocation.ensureCanDecideRequests();
        if (comment == null || comment.isBlank()) {
            throw new DomainValidationException(Map.of("comment", "La justificación del rechazo es obligatoria"));
        }
        request.setComment(comment.trim());
        request.setState(EnumStateRequest.REJECT);
    }

    @Override
    public void cancel(Request request) {
        // The student can withdraw it at any moment while it is not decided (CU "Cancelar solicitud").
        request.setState(EnumStateRequest.CANCELLED);
    }
}
