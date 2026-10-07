package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConditionRequirement;

import java.io.Serializable;
import java.util.Map;

public class ConvocationRequirement implements Serializable {
    private static final double MAX_GRADE = 5.0;

    private int id;
    private String name;
    private String requiredValue;
    private EnumConditionRequirement conditional;
    private String description;
    private int requirementId;
    private int convocationId;

    public ConvocationRequirement(){}

    public ConvocationRequirement(int id, String name, String requiredValue, EnumConditionRequirement conditional, String description, int requirementId, int convocationId) {
        this.id = id;
        this.name = name;
        this.requiredValue = requiredValue;
        this.conditional = conditional;
        this.description = description;
        this.requirementId = requirementId;
        this.convocationId = convocationId;
    }

    /**
     * The requirement type must exist; a numeric value must be greater than 0,
     * and a grade must be a number between 0 (exclusive) and 5.0.
     */
    public void validate(Map<Integer, Requirement> requirementCatalog, String field, Map<String, String> errors) {
        Requirement requirement = requirementCatalog.get(requirementId);
        if (requirement == null) {
            errors.put(field + ".requirementId", "El tipo de requisito " + requirementId + " no existe");
            return;
        }
        Double numericValue = parseNumber(requiredValue);
        if (requirement.isGrade() && numericValue == null) {
            errors.put(field + ".requiredValue", "El valor de \"" + requirement.getName() + "\" debe ser una nota numérica");
        } else if (numericValue != null && numericValue <= 0) {
            errors.put(field + ".requiredValue", "El valor de \"" + requirement.getName() + "\" debe ser mayor a 0");
        } else if (requirement.isGrade() && numericValue > MAX_GRADE) {
            errors.put(field + ".requiredValue", "El valor de \"" + requirement.getName() + "\" no puede ser mayor a 5.0");
        }
    }

    private static Double parseNumber(String value) {
        if (value == null) return null;
        try {
            double number = Double.parseDouble(value.trim().replace(',', '.'));
            return Double.isFinite(number) ? number : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(String requiredValue) {
        this.requiredValue = requiredValue;
    }

    public EnumConditionRequirement getConditional() {
        return conditional;
    }

    public void setConditional(EnumConditionRequirement conditional) {
        this.conditional = conditional;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    public int getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(int convocationId) {
        this.convocationId = convocationId;
    }

    @Override
    public String toString() {
        return "ConvocationRequirement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", requiredValue='" + requiredValue + '\'' +
                ", conditional=" + conditional +
                ", description='" + description + '\'' +
                ", requirementId=" + requirementId +
                ", convocationId=" + convocationId +
                '}';
    }
}
