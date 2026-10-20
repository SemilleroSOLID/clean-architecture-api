package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumStateRequest;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** One row of the list of requests the administrator reviews (issue #16). */
public class RequestSummaryDto implements Serializable {
    private int id;
    private int studentId;
    private String studentName;
    private int convocationId;
    private String convocationTitle;
    private LocalDate creationDate;
    private EnumStateRequest state;
    // Names of the requirements the student does not meet, so the pending ones are reviewed carefully.
    private List<String> unmetRequirements = new ArrayList<>();

    public RequestSummaryDto() {}

    public List<String> getUnmetRequirements() {
        return unmetRequirements;
    }

    public void setUnmetRequirements(List<String> unmetRequirements) {
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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
}
