package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;
import com.example.demo.Domain.Enums.EnumStateRequest;

import java.io.Serializable;
import java.time.LocalDate;

/** One application in the history of a student, with the data of its convocation (issue #18). */
public class StudentRequestDto implements Serializable {
    private int id;
    private LocalDate creationDate;
    private EnumStateRequest state;
    private String comment;
    private int convocationId;
    private String convocationTitle;
    private EnumConvocationType convocationType;
    private LocalDate convocationStartDate;
    private LocalDate convocationEndDate;
    private EnumConvocationState convocationState;

    public StudentRequestDto() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(int convocationId) {
        this.convocationId = convocationId;
    }

    public String getConvocationTitle() {
        return convocationTitle;
    }

    public void setConvocationTitle(String convocationTitle) {
        this.convocationTitle = convocationTitle;
    }

    public EnumConvocationType getConvocationType() {
        return convocationType;
    }

    public void setConvocationType(EnumConvocationType convocationType) {
        this.convocationType = convocationType;
    }

    public LocalDate getConvocationStartDate() {
        return convocationStartDate;
    }

    public void setConvocationStartDate(LocalDate convocationStartDate) {
        this.convocationStartDate = convocationStartDate;
    }

    public LocalDate getConvocationEndDate() {
        return convocationEndDate;
    }

    public void setConvocationEndDate(LocalDate convocationEndDate) {
        this.convocationEndDate = convocationEndDate;
    }

    public EnumConvocationState getConvocationState() {
        return convocationState;
    }

    public void setConvocationState(EnumConvocationState convocationState) {
        this.convocationState = convocationState;
    }
}
