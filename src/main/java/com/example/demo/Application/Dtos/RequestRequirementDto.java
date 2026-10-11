package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class RequestRequirementDto implements Serializable {
    private int id;
    @Positive(message = "El requisito de la convocatoria es obligatorio")
    private int convocationRequirementId;
    private String value;
    @Size(max = 500, message = "El adjunto no puede tener más de 500 caracteres")
    private String attachment;

    public RequestRequirementDto() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConvocationRequirementId() {
        return convocationRequirementId;
    }

    public void setConvocationRequirementId(int convocationRequirementId) {
        this.convocationRequirementId = convocationRequirementId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
