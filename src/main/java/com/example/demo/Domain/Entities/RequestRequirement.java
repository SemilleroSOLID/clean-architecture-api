package com.example.demo.Domain.Entities;

import java.io.File;

public class RequestRequirement {
    private int id;
    private String value;
    private File attachment;

    public RequestRequirement(){}

    public RequestRequirement(int id, String value, File attachment) {
        this.id = id;
        this.value = value;
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "RequestRequirement{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", attachment=" + attachment +
                '}';
    }
}
