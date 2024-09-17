package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumStateRequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Request implements Serializable {
    private int id;
    private Date creationDate;
    private EnumStateRequest state;
    private List<RequestRequirement> requestRequirementList;

    public Request(){}

    public Request(int id, Date creationDate, EnumStateRequest state, List<RequestRequirement> requestRequirementList) {
        this.id = id;
        this.creationDate = creationDate;
        this.state = state;
        this.requestRequirementList = requestRequirementList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public EnumStateRequest getState() {
        return state;
    }

    public void setState(EnumStateRequest state) {
        this.state = state;
    }

    public List<RequestRequirement> getRequestRequirementList() {
        return requestRequirementList;
    }

    public void setRequestRequirementList(List<RequestRequirement> requestRequirementList) {
        this.requestRequirementList = requestRequirementList;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", state=" + state +
                ", requestRequirementList=" + requestRequirementList +
                '}';
    }
}
