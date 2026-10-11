package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CancelConvocationDto {
    @NotBlank(message = "La justificación de la cancelación es obligatoria")
    @Size(max = 500, message = "La justificación no puede tener más de 500 caracteres")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
