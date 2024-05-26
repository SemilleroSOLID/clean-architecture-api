package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumConvocationType;

import java.util.Date;
import java.util.List;

public class Convocation {
    private int id;
    private String title;
    private String description;
    private EnumConvocationType type;
    private Date startDate;
    private Date endDate;
    private String state;
    private List<ConvocationRequirement> convocationRequirements;
    private List<Request> requestList;

    public Convocation(){}

    public Convocation(int id, String title, String description, EnumConvocationType type, Date startDate, Date endDate, String state, List<ConvocationRequirement> convocationRequirements, List<Request> requestList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.convocationRequirements = convocationRequirements;
        this.requestList = requestList;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public List<ConvocationRequirement> getConvocationRequirements() {
        return convocationRequirements;
    }

    public void setConvocationRequirements(List<ConvocationRequirement> convocationRequirements) {
        this.convocationRequirements = convocationRequirements;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
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
