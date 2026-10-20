package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumConditionRequirement;

import java.io.Serializable;

/** One requirement of a request: what the convocation requires, what the student gave and whether it is met. */
public class RequirementCheckDto implements Serializable {
    private int convocationRequirementId;
    private String name;
    private EnumConditionRequirement conditional;
    private String requiredValue;
    private String value;
    private String attachment;
    private boolean met;

    public RequirementCheckDto() {}

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

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public boolean isMet() {
        return met;
    }

    public void setMet(boolean met) {
        this.met = met;
    }
}
