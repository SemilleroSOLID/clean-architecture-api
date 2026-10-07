package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Enums.EnumConvocationType;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConvocationValidationTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 7);
    private static final Map<Integer, Requirement> CATALOG = Map.of(
            1, new Requirement(1, "Promedio académico superior a 4.0", true),
            3, new Requirement(3, "Haber cursado mínimo 2 semestres", false));

    private static Convocation convocation(LocalDate start, LocalDate end, ConvocationRequirement... requirements) {
        Convocation convocation = new Convocation();
        convocation.setTitle("Monitoria");
        convocation.setType(EnumConvocationType.MONITORING);
        convocation.setStartDate(start);
        convocation.setEndDate(end);
        convocation.setConvocationRequirements(List.of(requirements));
        return convocation;
    }

    private static ConvocationRequirement requirement(int requirementId, String value) {
        return new ConvocationRequirement(0, "Requisito", value, EnumConditionRequirement.GREATER_OR_EQUAL, "", requirementId, 0);
    }

    private static Map<String, String> errorsOf(Convocation convocation) {
        return assertThrows(DomainValidationException.class, () -> convocation.validate(TODAY, CATALOG)).getErrors();
    }

    @Test
    void validConvocationPasses() {
        assertDoesNotThrow(() -> convocation(TODAY.plusDays(1), TODAY.plusDays(2), requirement(1, "4.0"), requirement(3, "2"))
                .validate(TODAY, CATALOG));
    }

    @Test
    void startDateTodayOrPastIsRejected() {
        assertTrue(errorsOf(convocation(TODAY, TODAY.plusDays(5))).containsKey("startDate"));
        assertTrue(errorsOf(convocation(TODAY.minusDays(1), TODAY.plusDays(5))).containsKey("startDate"));
    }

    @Test
    void endDateMustBeAtLeastOneDayAfterStart() {
        assertTrue(errorsOf(convocation(TODAY.plusDays(3), TODAY.plusDays(3))).containsKey("endDate"));
        assertTrue(errorsOf(convocation(TODAY.plusDays(3), TODAY.plusDays(2))).containsKey("endDate"));
    }

    @Test
    void gradeOutOfRangeIsRejected() {
        for (String value : List.of("0", "-1", "5.1", "abc")) {
            Map<String, String> errors = errorsOf(convocation(TODAY.plusDays(1), TODAY.plusDays(2), requirement(1, value)));
            assertTrue(errors.containsKey("convocationRequirements[0].requiredValue"), "value " + value);
        }
    }

    @Test
    void gradeLimitsAreAccepted() {
        assertDoesNotThrow(() -> convocation(TODAY.plusDays(1), TODAY.plusDays(2), requirement(1, "5.0"), requirement(1, "0,1"))
                .validate(TODAY, CATALOG));
    }

    @Test
    void numericValueOfNonGradeMustBePositive() {
        Map<String, String> errors = errorsOf(convocation(TODAY.plusDays(1), TODAY.plusDays(2), requirement(3, "0")));
        assertTrue(errors.containsKey("convocationRequirements[0].requiredValue"));
        assertDoesNotThrow(() -> convocation(TODAY.plusDays(1), TODAY.plusDays(2), requirement(3, "10")).validate(TODAY, CATALOG));
    }

    @Test
    void unknownRequirementTypeIsRejected() {
        Map<String, String> errors = errorsOf(convocation(TODAY.plusDays(1), TODAY.plusDays(2), requirement(99, "1")));
        assertTrue(errors.containsKey("convocationRequirements[0].requirementId"));
    }

    @Test
    void editingKeepsAStartDateThatAlreadyPassed() {
        LocalDate originalStart = TODAY.minusDays(10);
        assertDoesNotThrow(() -> convocation(originalStart, TODAY.plusDays(5), requirement(1, "4.0"))
                .validateChanges(originalStart, TODAY, CATALOG));
    }

    @Test
    void editingToANewPastStartDateIsRejected() {
        Convocation edited = convocation(TODAY.minusDays(2), TODAY.plusDays(5));
        Map<String, String> errors = assertThrows(DomainValidationException.class,
                () -> edited.validateChanges(TODAY.minusDays(10), TODAY, CATALOG)).getErrors();
        assertTrue(errors.containsKey("startDate"));
    }

    @Test
    void allErrorsAreReportedTogether() {
        Map<String, String> errors = errorsOf(convocation(TODAY, TODAY, requirement(1, "6")));
        assertEquals(3, errors.size());
    }
}
