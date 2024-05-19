package com.example.demo.Domain.Enums;

public enum EnumConvocationType {
    MONITORING(1),
    GRANT(2),
    RESIDENCE(3);
    private int value;
    EnumConvocationType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
