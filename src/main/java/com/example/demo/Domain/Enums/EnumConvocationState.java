package com.example.demo.Domain.Enums;

public enum EnumConvocationState {
    OPEN(1),
    CLOSED(2);

    private final int value;

    EnumConvocationState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EnumConvocationState fromValue(int value) {
        for (EnumConvocationState state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ConvocationState value: " + value);
    }
}
