package com.example.demo.Application.Dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConvocationDto implements Serializable {
    private int id;
    @NotBlank(message = "El título es obligatorio")
    private String title;
    private String description;
    @NotNull(message = "El tipo de convocatoria es obligatorio")
    private EnumConvocationType type;
    @NotNull(message = "La fecha de inicio es obligatoria")
    private Date startDate;
    @NotNull(message = "La fecha de fin es obligatoria")
    private Date endDate;
    private EnumConvocationState state;
    @Valid
    private List<ConvocationRequirementDto> convocationRequirements;
    public ConvocationDto(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EnumConvocationType getType() {
        return type;
    }

    public void setType(EnumConvocationType type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public EnumConvocationState getState() {
        return state;
    }

    public void setState(EnumConvocationState state) {
        this.state = state;
    }

    public List<ConvocationRequirementDto> getConvocationRequirements() {
        return convocationRequirements;
    }

    public void setConvocationRequirements(List<ConvocationRequirementDto> convocationRequirements) {
        if(this.convocationRequirements == null) this.convocationRequirements = new ArrayList<>();
        this.convocationRequirements = convocationRequirements;
    }
}
