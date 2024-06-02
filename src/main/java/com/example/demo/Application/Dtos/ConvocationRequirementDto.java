package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumConditionConvocation;

import java.io.Serializable;

public class ConvocationRequirementDto implements Serializable {
    private int id;
    private String name;
    private String requiredValue;
    private EnumConditionConvocation conditional;
    private String description;
    private int requirementId;
    private int convocationId;

    public ConvocationRequirementDto() {}

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

    public EnumConditionConvocation getConditional() {
        return conditional;
    }

    public void setConditional(EnumConditionConvocation conditional) {
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
}
