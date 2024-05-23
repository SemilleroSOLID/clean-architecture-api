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
    private List<ConvocationRequirement> convocationRequirementList;
    private List<Request> requestList;

    public Convocation(){}

    public Convocation(int id, String title, String description, EnumConvocationType type, Date startDate, Date endDate, String state, List<ConvocationRequirement> convocationRequirementList, List<Request> requestList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.convocationRequirementList = convocationRequirementList;
        this.requestList = requestList;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public List<ConvocationRequirement> getConvocationRequirementList() {
        return convocationRequirementList;
    }

    public void setConvocationRequirementList(List<ConvocationRequirement> convocationRequirementList) {
        this.convocationRequirementList = convocationRequirementList;
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
}
