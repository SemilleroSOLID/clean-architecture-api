package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Exceptions.DomainValidationException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Application of a student to a convocation (CU-002). */
public class Request implements Serializable {
    private int id;
    private int studentId;
    private int convocationId;
    private LocalDate creationDate;
    private EnumStateRequest state;
    private List<RequestRequirement> requirements = new ArrayList<>();

    public Request(){}

    /**
     * The student must give exactly one non empty value for each requirement of the convocation,
     * and only for requirements of that convocation.
     */
    public void validateAgainst(List<ConvocationRequirement> convocationRequirements) {
        Map<String, String> errors = new LinkedHashMap<>();
        Map<Integer, ConvocationRequirement> byId = new LinkedHashMap<>();
        convocationRequirements.forEach(requirement -> byId.put(requirement.getId(), requirement));

        Set<Integer> answered = new HashSet<>();
        for (RequestRequirement answer : requirements) {
            int convocationRequirementId = answer.getConvocationRequirementId();
            String field = "requirements[" + convocationRequirementId + "]";
            if (!byId.containsKey(convocationRequirementId)) {
                errors.put(field, "El requisito " + convocationRequirementId + " no pertenece a esta convocatoria");
            } else if (!answered.add(convocationRequirementId)) {
                errors.put(field, "El requisito \"" + byId.get(convocationRequirementId).getName() + "\" se envió más de una vez");
            } else if (answer.getValue() == null || answer.getValue().isBlank()) {
                errors.put(field, "Falta el valor del requisito \"" + byId.get(convocationRequirementId).getName() + "\"");
            }
        }
        byId.forEach((id, requirement) -> {
            if (!answered.contains(id)) {
                errors.putIfAbsent("requirements[" + id + "]", "Falta el valor del requisito \"" + requirement.getName() + "\"");
            }
        });
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
    }

    /**
     * Automatic verification (issue #14): the request is ACTIVE when every requirement is met and PENDING
     * otherwise. It is never rejected automatically; an administrator decides the pending ones.
     * Call it after {@link #validateAgainst}, so every requirement has exactly one value.
     *
     * @return the convocation requirements the student does not meet, empty when all are met
     */
    public List<ConvocationRequirement> verifyRequirements(List<ConvocationRequirement> convocationRequirements) {
        Map<Integer, String> valueByRequirement = new HashMap<>();
        requirements.forEach(answer -> valueByRequirement.put(answer.getConvocationRequirementId(), answer.getValue()));

        List<ConvocationRequirement> unmet = convocationRequirements.stream()
                .filter(requirement -> !requirement.isMetBy(valueByRequirement.get(requirement.getId())))
                .toList();
        state = unmet.isEmpty() ? EnumStateRequest.ACTIVE : EnumStateRequest.PENDING;
        return unmet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(int convocationId) {
        this.convocationId = convocationId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public EnumStateRequest getState() {
        return state;
    }

    public void setState(EnumStateRequest state) {
        this.state = state;
    }

    public List<RequestRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequestRequirement> requirements) {
        this.requirements = requirements == null ? new ArrayList<>() : requirements;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", convocationId=" + convocationId +
                ", creationDate=" + creationDate +
                ", state=" + state +
                ", requirements=" + requirements +
                '}';
    }
}
