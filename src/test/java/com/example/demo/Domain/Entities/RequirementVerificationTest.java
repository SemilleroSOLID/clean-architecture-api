package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Enums.EnumStateRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequirementVerificationTest {

    private static ConvocationRequirement requirement(int id, String name, EnumConditionRequirement condition, String requiredValue) {
        return new ConvocationRequirement(id, name, requiredValue, condition, "", 1, 1);
    }

    private static RequestRequirement answer(int convocationRequirementId, String value) {
        return new RequestRequirement(0, 0, convocationRequirementId, value, null);
    }

    @Nested
    class Conditions {
        @Test
        void equal() {
            ConvocationRequirement semesters = requirement(1, "Semestres", EnumConditionRequirement.EQUAL, "3");
            assertTrue(semesters.isMetBy("3"));
            assertFalse(semesters.isMetBy("4"));
        }

        @Test
        void lessOrEqual() {
            ConvocationRequirement stratum = requirement(1, "Estrato", EnumConditionRequirement.LESS_OR_EQUAL, "3");
            assertTrue(stratum.isMetBy("3"));
            assertFalse(stratum.isMetBy("4"));
        }

        @Test
        void greaterOrEqual() {
            ConvocationRequirement average = requirement(1, "Promedio", EnumConditionRequirement.GREATER_OR_EQUAL, "4.0");
            assertTrue(average.isMetBy("4.0"));
            assertFalse(average.isMetBy("3.9"));
        }

        @Test
        void less() {
            ConvocationRequirement stratum = requirement(1, "Estrato", EnumConditionRequirement.LESS, "3");
            assertTrue(stratum.isMetBy("2"));
            assertFalse(stratum.isMetBy("3"));
        }

        @Test
        void greater() {
            ConvocationRequirement average = requirement(1, "Promedio", EnumConditionRequirement.GREATER, "4.0");
            assertTrue(average.isMetBy("4.1"));
            assertFalse(average.isMetBy("4.0"));
        }
    }

    @Nested
    class Values {
        @Test
        void numbersAreComparedAsNumbers() {
            ConvocationRequirement average = requirement(1, "Promedio", EnumConditionRequirement.EQUAL, "4");
            assertTrue(average.isMetBy("4.0"));
            assertTrue(average.isMetBy(" 4,0 "));
        }

        @Test
        void textIsComparedWithoutCaseOnlyWithEqual() {
            ConvocationRequirement program = requirement(1, "Programa", EnumConditionRequirement.EQUAL, "Ingeniería de Sistemas");
            assertTrue(program.isMetBy("  ingeniería de sistemas "));
            assertFalse(program.isMetBy("Biología"));

            ConvocationRequirement greaterText = requirement(2, "Programa", EnumConditionRequirement.GREATER, "A");
            assertFalse(greaterText.isMetBy("B"));
        }

        @Test
        void textAgainstANumberIsNotMet() {
            ConvocationRequirement average = requirement(1, "Promedio", EnumConditionRequirement.GREATER_OR_EQUAL, "4.0");
            assertFalse(average.isMetBy("alto"));
        }
    }

    @Nested
    class RequestVerification {
        private final List<ConvocationRequirement> convocationRequirements = List.of(
                requirement(10, "Promedio", EnumConditionRequirement.GREATER_OR_EQUAL, "4.0"),
                requirement(11, "Estrato", EnumConditionRequirement.LESS_OR_EQUAL, "3"));

        private Request request(RequestRequirement... answers) {
            Request request = new Request();
            request.setRequirements(List.of(answers));
            return request;
        }

        @Test
        void meetingEveryRequirementLeavesItActive() {
            Request request = request(answer(10, "4.5"), answer(11, "2"));
            List<ConvocationRequirement> unmet = request.verifyRequirements(convocationRequirements);
            assertTrue(unmet.isEmpty());
            assertEquals(EnumStateRequest.ACTIVE, request.getState());
        }

        @Test
        void failingARequirementLeavesItPendingAndReportsIt() {
            Request request = request(answer(10, "3.8"), answer(11, "2"));
            List<ConvocationRequirement> unmet = request.verifyRequirements(convocationRequirements);
            assertEquals(List.of("Promedio"), unmet.stream().map(ConvocationRequirement::getName).toList());
            assertEquals(EnumStateRequest.PENDING, request.getState());
        }

        @Test
        void unmetRequirementsDoesNotChangeTheStateOfADecidedRequest() {
            Request request = request(answer(10, "3.8"), answer(11, "2"));
            request.setState(EnumStateRequest.ACCEPT);
            List<ConvocationRequirement> unmet = request.unmetRequirements(convocationRequirements);
            assertEquals(List.of("Promedio"), unmet.stream().map(ConvocationRequirement::getName).toList());
            assertEquals(EnumStateRequest.ACCEPT, request.getState());
            assertEquals("3.8", request.valueOf(10));
            assertNull(request.valueOf(99));
        }

        @Test
        void failingEveryRequirementIsStillPendingAndNeverRejected() {
            Request request = request(answer(10, "2.0"), answer(11, "6"));
            List<ConvocationRequirement> unmet = request.verifyRequirements(convocationRequirements);
            assertEquals(2, unmet.size());
            assertEquals(EnumStateRequest.PENDING, request.getState());
            assertNotEquals(EnumStateRequest.REJECT, request.getState());
        }
    }
}
