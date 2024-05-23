package com.example.demo.Domain.Enums;

public enum EnumConditionConvocation {
    EQUAL(1),
    LESS_OR_EQUAL(2),
    GREATER_OR_EQUAL(3),
    LESS(4),
    GREATER(5);

    private int value;
    EnumConditionConvocation(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
