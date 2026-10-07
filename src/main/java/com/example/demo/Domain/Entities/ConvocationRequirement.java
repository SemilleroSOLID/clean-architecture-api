package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConditionRequirement;

import java.io.Serializable;

public class ConvocationRequirement implements Serializable {
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
