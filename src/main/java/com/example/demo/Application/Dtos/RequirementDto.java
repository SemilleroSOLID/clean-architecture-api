package com.example.demo.Application.Dtos;

public class RequirementDto {
    private int id;
    private String requirementName;
    private boolean grade;

    public RequirementDto(int id, String requirementName, boolean grade) {
        this.id = id;
        this.requirementName = requirementName;
        this.grade = grade;
    }

    public boolean isGrade() {
        return grade;
    }

    public void setGrade(boolean grade) {
        this.grade = grade;
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
