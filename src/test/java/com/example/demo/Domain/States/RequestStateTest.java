package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Exceptions.InvalidRequestStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestStateTest {

    private static Request request(EnumStateRequest state) {
        Request request = new Request();
        request.setStudentId(7);
        request.setState(state);
        return request;
    }

    private static Convocation closedConvocation() {
        Convocation convocation = new Convocation();
        convocation.setStartDate(LocalDate.of(2026, 10, 1));
        convocation.setEndDate(LocalDate.of(2026, 10, 31));
        convocation.setState(EnumConvocationState.CLOSED);
        return convocation;
    }

    private static void assertNotAllowed(Executable action, String expectedMessage) {
        assertEquals(expectedMessage, assertThrows(InvalidRequestStateException.class, action).getErrors().get("state"));
    }

    @Test
    void aPendingOrActiveRequestCanBeWithdrawn() {
        for (EnumStateRequest state : EnumStateRequest.UNDECIDED) {
            Request request = request(state);
            request.cancel();
            assertEquals(EnumStateRequest.CANCELLED, request.getState());
        }
    }

    @Test
    void aDecidedRequestCannotBeWithdrawn() {
        assertNotAllowed(() -> request(EnumStateRequest.ACCEPT).cancel(), "No se puede retirar una solicitud aprobada");
        assertNotAllowed(() -> request(EnumStateRequest.REJECT).cancel(), "No se puede retirar una solicitud rechazada");
    }

    @Test
    void aWithdrawnRequestIsFinal() {
        Request request = request(EnumStateRequest.CANCELLED);
        assertNotAllowed(request::cancel, "No se puede retirar una solicitud retirada");
        assertNotAllowed(() -> request.approve(closedConvocation()), "No se puede aprobar una solicitud retirada");
        assertNotAllowed(() -> request.reject(closedConvocation(), "motivo"), "No se puede rechazar una solicitud retirada");
        assertEquals(EnumStateRequest.CANCELLED, request.getState());
    }

    @Test
    void aWithdrawnRequestNoLongerWaitsForADecision() {
        assertFalse(EnumStateRequest.UNDECIDED.contains(EnumStateRequest.CANCELLED));
    }

    @Test
    void eachStateObjectMatchesItsValue() {
        for (EnumStateRequest state : EnumStateRequest.values()) {
            assertEquals(state, RequestStates.of(state).value());
        }
    }

    @Test
    void onlyItsStudentOwnsTheRequest() {
        assertTrue(request(EnumStateRequest.PENDING).belongsTo(7));
        assertFalse(request(EnumStateRequest.PENDING).belongsTo(8));
    }
}
