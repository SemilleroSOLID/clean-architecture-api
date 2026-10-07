package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Convocation implements Serializable {
    private int id;
    private String title;
    private String description;
    private EnumConvocationType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private EnumConvocationState state;
    private List<ConvocationRequirement> convocationRequirements = new ArrayList<>();
    private List<Request> requestList = new ArrayList<>();

    public Convocation(){}

    public Convocation(int id, String title, String description, EnumConvocationType type, LocalDate startDate, LocalDate endDate, EnumConvocationState state, List<ConvocationRequirement> convocationRequirements) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.setConvocationRequirements(convocationRequirements);
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList == null ? new ArrayList<>() : requestList;
    }

    public List<ConvocationRequirement> getConvocationRequirements() {
        return convocationRequirements;
    }

    public void setConvocationRequirements(List<ConvocationRequirement> convocationRequirements) {
        this.convocationRequirements = convocationRequirements == null ? new ArrayList<>() : convocationRequirements;
    }

    public EnumConvocationState getState() {
        return state;
    }

    public void setState(EnumConvocationState state) {
        this.state = state;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public EnumConvocationType getType() {
        return type;
    }

    public void setType(EnumConvocationType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return "Convocation{" +
                "id: " + id + "\n" +
                "Title: " + title + "\n" +
                "Description: " + description + "\n" +
                "Type: " + type + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "State: " + state + "\n" +
                "Convocation Requirement List: " + convocationRequirements + "\n" +
                "Request List: " + requestList + "\n";
    }
}
