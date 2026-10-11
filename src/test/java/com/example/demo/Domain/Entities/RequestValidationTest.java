package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestValidationTest {
    private static final List<ConvocationRequirement> CONVOCATION_REQUIREMENTS = List.of(
            new ConvocationRequirement(10, "Promedio", "4.0", EnumConditionRequirement.GREATER_OR_EQUAL, "", 1, 1),
            new ConvocationRequirement(11, "Semestres", "2", EnumConditionRequirement.GREATER_OR_EQUAL, "", 3, 1));

    private static RequestRequirement answer(int convocationRequirementId, String value) {
        return new RequestRequirement(0, 0, convocationRequirementId, value, null);
    }

    private static Map<String, String> errorsOf(RequestRequirement... answers) {
        Request request = new Request();
        request.setRequirements(List.of(answers));
        return assertThrows(DomainValidationException.class, () -> request.validateAgainst(CONVOCATION_REQUIREMENTS)).getErrors();
    }

    @Test
    void aValueForEachRequirementIsValid() {
        Request request = new Request();
        request.setRequirements(List.of(answer(10, "4.5"), answer(11, "3")));
        assertDoesNotThrow(() -> request.validateAgainst(CONVOCATION_REQUIREMENTS));
    }

    @Test
    void missingRequirementIsReportedByName() {
        Map<String, String> errors = errorsOf(answer(10, "4.5"));
        assertEquals("Falta el valor del requisito \"Semestres\"", errors.get("requirements[11]"));
    }

    @Test
    void blankValueCountsAsMissing() {
        Map<String, String> errors = errorsOf(answer(10, "  "), answer(11, "3"));
        assertEquals("Falta el valor del requisito \"Promedio\"", errors.get("requirements[10]"));
    }

    @Test
    void requirementOfAnotherConvocationIsRejected() {
        Map<String, String> errors = errorsOf(answer(10, "4.5"), answer(11, "3"), answer(99, "x"));
        assertEquals("El requisito 99 no pertenece a esta convocatoria", errors.get("requirements[99]"));
    }

    @Test
    void sameRequirementTwiceIsRejected() {
        Map<String, String> errors = errorsOf(answer(10, "4.5"), answer(10, "4.0"), answer(11, "3"));
        assertEquals("El requisito \"Promedio\" se envió más de una vez", errors.get("requirements[10]"));
    }

    @Test
    void emptyRequestReportsEveryRequirement() {
        assertEquals(2, errorsOf().size());
    }
}
