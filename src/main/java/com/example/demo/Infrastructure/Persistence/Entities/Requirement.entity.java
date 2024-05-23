package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Requirement", schema = "dbo")
public class RequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "requirementName", nullable = false, length = 255)
    private String requirementName;

    @OneToMany(mappedBy="requirement")
    private Set<ConvocationRequirementEntity> convocationRequirements;

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

    public Set<ConvocationRequirementEntity> getConvocationRequirements() {
        return convocationRequirements;
    }

    public void setConvocationRequirements(Set<ConvocationRequirementEntity> convocationRequirements) {
        this.convocationRequirements = convocationRequirements;
    }

}