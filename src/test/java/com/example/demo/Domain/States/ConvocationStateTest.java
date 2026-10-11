package com.example.demo.Domain.States;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import com.example.demo.Domain.Exceptions.InvalidConvocationStateException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConvocationStateTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 10);

    private static Convocation convocation(EnumConvocationState state, LocalDate start, LocalDate end) {
        Convocation convocation = new Convocation();
        convocation.setTitle("Monitoria");
        convocation.setType(EnumConvocationType.MONITORING);
        convocation.setStartDate(start);
        convocation.setEndDate(end);
        convocation.setState(state);
        return convocation;
    }

    private static Convocation convocation(EnumConvocationState state) {
        return convocation(state, TODAY.minusDays(5), TODAY.plusDays(5));
    }

    private static void assertNotAllowed(Executable action, String expectedMessage) {
        InvalidConvocationStateException exception = assertThrows(InvalidConvocationStateException.class, action);
        assertEquals(expectedMessage, exception.getErrors().get("state"));
    }

    @Nested
    class Open {
        @Test
        void closeMovesToClosed() {
            Convocation convocation = convocation(EnumConvocationState.OPEN);
            convocation.close();
            assertEquals(EnumConvocationState.CLOSED, convocation.getState());
        }

        @Test
        void cancelWithReasonMovesToCancelled() {
            Convocation convocation = convocation(EnumConvocationState.OPEN);
            convocation.cancel("  Falta de presupuesto  ");
            assertEquals(EnumConvocationState.CANCELLED, convocation.getState());
            assertEquals("Falta de presupuesto", convocation.getCancellationReason());
        }

        @Test
        void cancelWithoutReasonIsRejected() {
            Convocation convocation = convocation(EnumConvocationState.OPEN);
            DomainValidationException exception = assertThrows(DomainValidationException.class, () -> convocation.cancel(" "));
            assertTrue(exception.getErrors().containsKey("reason"));
            assertEquals(EnumConvocationState.OPEN, convocation.getState());
        }

        @Test
        void canBeEditedAndDeleted() {
            Convocation convocation = convocation(EnumConvocationState.OPEN);
            assertDoesNotThrow(convocation::ensureCanBeEdited);
            assertDoesNotThrow(convocation::ensureCanBeDeleted);
        }

        @Test
        void cannotBeReopenedOrFinished() {
            Convocation convocation = convocation(EnumConvocationState.OPEN);
            assertNotAllowed(() -> convocation.reopen(TODAY.plusDays(10), TODAY), "No se puede reabrir una convocatoria abierta");
            assertNotAllowed(() -> convocation.finish(false), "No se puede finalizar una convocatoria abierta");
        }

        @Test
        void receivesRequestsOnlyBetweenItsDates() {
            assertTrue(convocation(EnumConvocationState.OPEN, TODAY, TODAY.plusDays(3)).canReceiveRequests(TODAY));
            assertTrue(convocation(EnumConvocationState.OPEN, TODAY.minusDays(3), TODAY).canReceiveRequests(TODAY));
            assertFalse(convocation(EnumConvocationState.OPEN, TODAY.plusDays(1), TODAY.plusDays(3)).canReceiveRequests(TODAY));
        }

        @Test
        void requestsAreNotDecidedWhileOpen() {
            assertFalse(convocation(EnumConvocationState.OPEN).canDecideRequests());
        }

        @Test
        void closesAutomaticallyWhenTheEndDatePassed() {
            Convocation expired = convocation(EnumConvocationState.OPEN, TODAY.minusDays(10), TODAY.minusDays(1));
            expired.applyDateRules(TODAY);
            assertEquals(EnumConvocationState.CLOSED, expired.getState());

            Convocation lastDay = convocation(EnumConvocationState.OPEN, TODAY.minusDays(10), TODAY);
            lastDay.applyDateRules(TODAY);
            assertEquals(EnumConvocationState.OPEN, lastDay.getState());
        }
    }

    @Nested
    class Closed {
        @Test
        void reopenWithAFutureEndDateMovesToOpen() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED, TODAY.minusDays(10), TODAY.minusDays(1));
            convocation.reopen(TODAY.plusDays(7), TODAY);
            assertEquals(EnumConvocationState.OPEN, convocation.getState());
            assertEquals(TODAY.plusDays(7), convocation.getEndDate());
        }

        @Test
        void reopenNeedsAnEndDateAfterToday() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED, TODAY.minusDays(10), TODAY.minusDays(1));
            DomainValidationException exception = assertThrows(DomainValidationException.class, () -> convocation.reopen(TODAY, TODAY));
            assertTrue(exception.getErrors().containsKey("endDate"));
            assertEquals(EnumConvocationState.CLOSED, convocation.getState());
        }

        @Test
        void reopenedConvocationDoesNotCloseAgainAutomatically() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED, TODAY.minusDays(10), TODAY.minusDays(1));
            convocation.reopen(TODAY.plusDays(7), TODAY);
            convocation.applyDateRules(TODAY);
            assertEquals(EnumConvocationState.OPEN, convocation.getState());
        }

        @Test
        void finishWhenEverythingIsDecided() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED);
            convocation.finish(false);
            assertEquals(EnumConvocationState.FINISHED, convocation.getState());
        }

        @Test
        void finishWithUndecidedRequestsIsRejected() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED);
            assertNotAllowed(() -> convocation.finish(true), "No se puede finalizar: hay solicitudes sin aprobar ni rechazar");
            assertEquals(EnumConvocationState.CLOSED, convocation.getState());
        }

        @Test
        void cancelMovesToCancelled() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED);
            convocation.cancel("Se suspendió el programa");
            assertEquals(EnumConvocationState.CANCELLED, convocation.getState());
        }

        @Test
        void requestsAreDecidedOnlyWhenClosed() {
            assertTrue(convocation(EnumConvocationState.CLOSED).canDecideRequests());
            assertFalse(convocation(EnumConvocationState.CLOSED).canReceiveRequests(TODAY));
        }

        @Test
        void cannotBeEditedDeletedOrClosedAgain() {
            Convocation convocation = convocation(EnumConvocationState.CLOSED);
            assertNotAllowed(convocation::ensureCanBeEdited, "No se puede editar una convocatoria cerrada");
            assertNotAllowed(convocation::ensureCanBeDeleted, "No se puede eliminar una convocatoria cerrada");
            assertNotAllowed(convocation::close, "No se puede cerrar una convocatoria cerrada");
        }
    }

    @Nested
    class FinalStates {
        private void assertReadOnly(EnumConvocationState state, String label) {
            Convocation convocation = convocation(state);
            assertNotAllowed(convocation::ensureCanBeEdited, "No se puede editar una convocatoria " + label);
            assertNotAllowed(convocation::ensureCanBeDeleted, "No se puede eliminar una convocatoria " + label);
            assertNotAllowed(convocation::close, "No se puede cerrar una convocatoria " + label);
            assertNotAllowed(() -> convocation.reopen(TODAY.plusDays(5), TODAY), "No se puede reabrir una convocatoria " + label);
            assertNotAllowed(() -> convocation.finish(false), "No se puede finalizar una convocatoria " + label);
            assertNotAllowed(() -> convocation.cancel("motivo"), "No se puede cancelar una convocatoria " + label);
            assertFalse(convocation.canReceiveRequests(TODAY));
            assertFalse(convocation.canDecideRequests());
            assertEquals(state, convocation.getState());
        }

        @Test
        void finishedIsReadOnly() {
            assertReadOnly(EnumConvocationState.FINISHED, "finalizada");
        }

        @Test
        void cancelledIsReadOnly() {
            assertReadOnly(EnumConvocationState.CANCELLED, "cancelada");
        }

        @Test
        void finalStatesDoNotChangeWithTheDates() {
            Convocation convocation = convocation(EnumConvocationState.FINISHED, TODAY.minusDays(10), TODAY.minusDays(1));
            convocation.applyDateRules(TODAY);
            assertEquals(EnumConvocationState.FINISHED, convocation.getState());
        }
    }

    @Nested
    class RequirementRules {
        private ConvocationRequirement requirement(int id, String value, String name) {
            return new ConvocationRequirement(id, name, value, EnumConditionRequirement.GREATER_OR_EQUAL, "", 1, 1);
        }

        @Test
        void namesAndDescriptionsCanChangeButNotTheRules() {
            Convocation convocation = convocation(EnumConvocationState.OPEN);
            List<ConvocationRequirement> current = List.of(requirement(1, "4.0", "Promedio"));

            convocation.setConvocationRequirements(List.of(requirement(1, "4.0", "Promedio académico")));
            assertDoesNotThrow(() -> convocation.ensureRequirementRulesUnchanged(current));

            convocation.setConvocationRequirements(List.of(requirement(1, "4.5", "Promedio")));
            assertThrows(DomainValidationException.class, () -> convocation.ensureRequirementRulesUnchanged(current));
        }
    }
}
