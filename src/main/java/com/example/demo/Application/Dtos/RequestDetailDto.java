package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;
import com.example.demo.Domain.Enums.EnumStateRequest;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** A request with its student, its convocation and the verification requirement by requirement (issue #16). */
public class RequestDetailDto implements Serializable {
    private int id;
    private LocalDate creationDate;
    private EnumStateRequest state;
    private String comment;
    private StudentDto student;
    private int convocationId;
    private String convocationTitle;
    private EnumConvocationType convocationType;
    private EnumConvocationState convocationState;
    private List<RequirementCheckDto> requirements = new ArrayList<>();

    public RequestDetailDto() {}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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

    public StudentDto getStudent() {
        return student;
    }

    public void setStudent(StudentDto student) {
        this.student = student;
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

    public EnumConvocationState getConvocationState() {
        return convocationState;
    }

    public void setConvocationState(EnumConvocationState convocationState) {
        this.convocationState = convocationState;
    }

    public List<RequirementCheckDto> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementCheckDto> requirements) {
        this.requirements = requirements;
    }
}
