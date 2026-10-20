package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumConditionRequirement;

import java.io.Serializable;

/** A requirement the student does not meet: what was required and what the student gave (issue #14). */
public class UnmetRequirementDto implements Serializable {
    private int convocationRequirementId;
    private String name;
    private EnumConditionRequirement conditional;
    private String requiredValue;
    private String value;

    public UnmetRequirementDto() {}

    public UnmetRequirementDto(int convocationRequirementId, String name, EnumConditionRequirement conditional,
                               String requiredValue, String value) {
        this.convocationRequirementId = convocationRequirementId;
        this.name = name;
        this.conditional = conditional;
        this.requiredValue = requiredValue;
        this.value = value;
    }

    public int getConvocationRequirementId() {
        return convocationRequirementId;
    }

    public void setConvocationRequirementId(int convocationRequirementId) {
        this.convocationRequirementId = convocationRequirementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumConditionRequirement getConditional() {
        return conditional;
    }

    public void setConditional(EnumConditionRequirement conditional) {
        this.conditional = conditional;
    }

    public String getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(String requiredValue) {
        this.requiredValue = requiredValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
