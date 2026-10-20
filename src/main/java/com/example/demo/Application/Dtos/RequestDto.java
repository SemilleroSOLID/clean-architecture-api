package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumStateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RequestDto implements Serializable {
    private int id;
    @Positive(message = "El estudiante es obligatorio")
    private int studentId;
    @Positive(message = "La convocatoria es obligatoria")
    private int convocationId;
    private LocalDate creationDate;
    private EnumStateRequest state;
    @NotNull(message = "Los valores de los requisitos son obligatorios")
    @Valid
    private List<RequestRequirementDto> requirements = new ArrayList<>();
    // Only in the response: result of the automatic verification (issue #14).
    private List<UnmetRequirementDto> unmetRequirements = new ArrayList<>();

    public RequestDto() {}

    public List<UnmetRequirementDto> getUnmetRequirements() {
        return unmetRequirements;
    }

    public void setUnmetRequirements(List<UnmetRequirementDto> unmetRequirements) {
        this.unmetRequirements = unmetRequirements;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(int convocationId) {
        this.convocationId = convocationId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public EnumStateRequest getState() {
        return state;
    }

    public void setState(EnumStateRequest state) {
        this.state = state;
    }

    public List<RequestRequirementDto> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequestRequirementDto> requirements) {
        this.requirements = requirements;
    }
}
