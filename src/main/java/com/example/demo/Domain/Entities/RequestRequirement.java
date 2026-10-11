package com.example.demo.Domain.Entities;

import java.io.Serializable;

/** Value a student gives for one requirement of the convocation. */
public class RequestRequirement implements Serializable {
    private int id;
    private int requestId;
    private int convocationRequirementId;
    private String value;
    private String attachment;

    public RequestRequirement(){}

    public RequestRequirement(int id, int requestId, int convocationRequirementId, String value, String attachment) {
        this.id = id;
        this.requestId = requestId;
        this.convocationRequirementId = convocationRequirementId;
        this.value = value;
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    @Override
    public String toString() {
        return "RequestRequirement{" +
                "id=" + id +
                ", convocationRequirementId=" + convocationRequirementId +
                ", value='" + value + '\'' +
                ", attachment='" + attachment + '\'' +
                '}';
    }
}
