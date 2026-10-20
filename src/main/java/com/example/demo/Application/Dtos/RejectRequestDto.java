package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/** Body of PUT /request/rejectRequest/{id}: the justification the student will read (issue #17). */
public class RejectRequestDto implements Serializable {
    @NotBlank(message = "La justificación del rechazo es obligatoria")
    @Size(max = 500, message = "La justificación no puede tener más de 500 caracteres")
    private String comment;

    public RejectRequestDto() {}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
