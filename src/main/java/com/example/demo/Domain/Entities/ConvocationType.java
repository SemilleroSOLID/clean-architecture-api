package com.example.demo.Domain.Entities;

public class ConvocationType {
    private  int id;
    private String convocationTypeName;

    public ConvocationType(int id, String convocationTypeName) {
        this.id = id;
        this.convocationTypeName = convocationTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConvocationTypeName() {
        return convocationTypeName;
    }

    public void setConvocationTypeName(String convocationTypeName) {
        this.convocationTypeName = convocationTypeName;
    }
}
