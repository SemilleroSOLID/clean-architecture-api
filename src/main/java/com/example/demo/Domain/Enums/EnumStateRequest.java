package com.example.demo.Domain.Enums;

public enum EnumStateRequest {
    PENDING(1),
    ACTIVE(2),
    REJECT(3),
    ACCEPT(4);
    private int value;
    EnumStateRequest(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
