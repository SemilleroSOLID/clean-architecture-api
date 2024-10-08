package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ConvocationRequirement", schema = "dbo")
public class ConvocationRequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

     @Column(name = "requiredValue", nullable = false, length = 255)
    private String requiredValue;

     @Column(name = "conditionalId", nullable = false)
    private Integer conditionalId;

     @Column(name = "requirementDescription", nullable = false, length = 255)
    private String requirementDescription;

     @Column(name = "convocationId", nullable = false)
    private Integer convocationId;

     @Column(name = "requirementId", nullable = false)
    private Integer requirementId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getConditionalId() {
        return conditionalId;
    }

    public void setConditionalId(Integer conditionalId) {
        this.conditionalId = conditionalId;
    }

    public String getRequirementDescription() {
        return requirementDescription;
    }

    public void setRequirementDescription(String requirementDescription) {
        this.requirementDescription = requirementDescription;
    }

    public Integer getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(Integer convocationId) {
        this.convocationId = convocationId;
    }

    public Integer getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Integer requirementId) {
        this.requirementId = requirementId;
    }


}