package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestDecisionTest {

    private static Convocation convocation(EnumConvocationState state) {
        Convocation convocation = new Convocation();
        convocation.setStartDate(LocalDate.of(2026, 10, 1));
        convocation.setEndDate(LocalDate.of(2026, 10, 31));
        convocation.setState(state);
        return convocation;
    }

    private static Request request(EnumStateRequest state) {
        Request request = new Request();
        request.setState(state);
        return request;
    }

    private static String errorOf(Executable action, String field) {
        return assertThrows(DomainValidationException.class, action).getErrors().get(field);
    }

    @Test
    void approvingAPendingRequestOfAClosedConvocationAcceptsIt() {
        Request request = request(EnumStateRequest.PENDING);
        request.approve(convocation(EnumConvocationState.CLOSED));
        assertEquals(EnumStateRequest.ACCEPT, request.getState());
        assertNull(request.getComment());
    }

    @Test
    void rejectingAnActiveRequestKeepsTheJustification() {
        Request request = request(EnumStateRequest.ACTIVE);
        request.reject(convocation(EnumConvocationState.CLOSED), "  Ya se cubrieron los cupos  ");
        assertEquals(EnumStateRequest.REJECT, request.getState());
        assertEquals("Ya se cubrieron los cupos", request.getComment());
    }

    @Test
    void rejectingWithoutJustificationIsNotAllowed() {
        Request request = request(EnumStateRequest.PENDING);
        assertEquals("La justificación del rechazo es obligatoria",
                errorOf(() -> request.reject(convocation(EnumConvocationState.CLOSED), "   "), "comment"));
        assertEquals(EnumStateRequest.PENDING, request.getState());
    }

    @Test
    void aDecidedRequestCannotBeDecidedAgain() {
        assertEquals("No se puede aprobar una solicitud que ya fue rechazada",
                errorOf(() -> request(EnumStateRequest.REJECT).approve(convocation(EnumConvocationState.CLOSED)), "state"));
        assertEquals("No se puede rechazar una solicitud que ya fue aprobada",
                errorOf(() -> request(EnumStateRequest.ACCEPT).reject(convocation(EnumConvocationState.CLOSED), "motivo"), "state"));
    }

    @Test
    void requestsOfAnOpenConvocationAreNotDecidedUntilItIsClosed() {
        Request request = request(EnumStateRequest.PENDING);
        assertEquals("La convocatoria sigue abierta: ciérrela para poder aprobar o rechazar sus solicitudes",
                errorOf(() -> request.approve(convocation(EnumConvocationState.OPEN)), "state"));
        assertEquals(EnumStateRequest.PENDING, request.getState());
    }

    @Test
    void requestsOfAFinishedOrCancelledConvocationAreNotDecided() {
        assertEquals("No se pueden decidir solicitudes de una convocatoria finalizada",
                errorOf(() -> request(EnumStateRequest.PENDING).approve(convocation(EnumConvocationState.FINISHED)), "state"));
        assertEquals("No se pueden decidir solicitudes de una convocatoria cancelada",
                errorOf(() -> request(EnumStateRequest.ACTIVE).reject(convocation(EnumConvocationState.CANCELLED), "motivo"), "state"));
    }
}
