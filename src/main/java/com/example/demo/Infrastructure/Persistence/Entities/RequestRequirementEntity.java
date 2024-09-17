package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

import java.io.File;

@Entity
@Table(name = "RequestRequirement", schema = "dbo")
public class RequestRequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "requestValue", nullable = false, length = 255)
    private String requestValue;

     @Column(name = "attachment", nullable = false)
    private File attachment;

     @Column(name = "requestId", nullable = false)
    private Integer requestId;

     @Column(name = "convocationRequirementId", nullable = false)
    private Integer convocationRequirementId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public void setRequestValue(String requestValue) {
        this.requestValue = requestValue;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getConvocationRequirementId() {
        return convocationRequirementId;
    }

    public void setConvocationRequirementId(Integer convocationRequirementId) {
        this.convocationRequirementId = convocationRequirementId;
    }
}