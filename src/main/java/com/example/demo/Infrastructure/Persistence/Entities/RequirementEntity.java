package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Requirement", schema = "dbo")
public class RequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirementId")
    private Integer id;

    @Column(name = "requirementName", nullable = false, length = 255)
    private String requirementName;

    @Column(name = "isGrade", nullable = false)
    private Boolean grade;

    public Boolean getGrade() {
        return grade;
    }

    public void setGrade(Boolean grade) {
        this.grade = grade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequirementName() {
        return requirementName;
    }

    public void setRequirementName(String requirementName) {
        this.requirementName = requirementName;
    }

}