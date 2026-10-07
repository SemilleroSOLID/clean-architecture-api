package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.example.demo.Domain.Enums.EnumConditionRequirement;

import java.io.Serializable;

public class ConvocationRequirementDto implements Serializable {
    private int id;
    @NotBlank(message = "El nombre del requisito es obligatorio")
    private String name;
    @NotBlank(message = "El valor requerido es obligatorio")
    private String requiredValue;
    @NotNull(message = "La condición del requisito es obligatoria")
    private EnumConditionRequirement conditional;
    private String description;
    @Positive(message = "El tipo de requisito es obligatorio")
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
}
