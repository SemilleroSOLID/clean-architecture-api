package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReopenConvocationDto {
    @NotNull(message = "La nueva fecha de fin es obligatoria")
    private LocalDate endDate;

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
