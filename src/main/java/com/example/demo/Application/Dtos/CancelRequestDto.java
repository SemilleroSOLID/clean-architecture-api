package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/** Body of PUT /request/cancelRequest/{id}. The student will come from the login once #22 exists. */
public class CancelRequestDto implements Serializable {
    @Positive(message = "El estudiante es obligatorio")
    private int studentId;

    public CancelRequestDto() {}

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
