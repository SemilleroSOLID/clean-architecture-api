package com.example.demo.Application.Dtos;

public class ConditionRequirementDto {
    private int id;
    private String conditionRequirementName;

    public ConditionRequirementDto(int id, String conditionRequirementName) {
        this.id = id;
        this.conditionRequirementName = conditionRequirementName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConditionRequirementName() {
        return conditionRequirementName;
    }

    public void setConditionRequirementName(String conditionRequirementName) {
        this.conditionRequirementName = conditionRequirementName;
    }
}
