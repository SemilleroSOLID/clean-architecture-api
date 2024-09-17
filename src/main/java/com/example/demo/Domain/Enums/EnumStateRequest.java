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
    public static EnumStateRequest fromValue(int value) {
        for (EnumStateRequest state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ConvocationState value: " + value);
    }
}
