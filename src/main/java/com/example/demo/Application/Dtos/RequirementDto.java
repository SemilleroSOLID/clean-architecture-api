package com.example.demo.Application.Dtos;

public class RequirementDto {
    private int id;
    private String requirementName;

    public RequirementDto(int id, String requirementName) {
        this.id = id;
        this.requirementName = requirementName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequirementName() {
        return requirementName;
    }

    public void setRequirementName(String requirementName) {
        this.requirementName = requirementName;
    }
}
