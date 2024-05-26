package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConditionConvocation;

public class ConvocationRequirement {
    private int id;
    private String name;
    private String requiredValue;
    private EnumConditionConvocation conditional;
    private String description;

    public ConvocationRequirement(){}

    public ConvocationRequirement(int id, String name, String requiredValue, EnumConditionConvocation conditional, String description) {
        this.id = id;
        this.name = name;
        this.requiredValue = requiredValue;
        this.conditional = conditional;
        this.description = description;
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

    @Override
    public String toString() {
        return "ConvocationRequirement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", requiredValue='" + requiredValue + '\'' +
                ", conditional=" + conditional +
                ", description='" + description + '\'' +
                '}';
    }
}
