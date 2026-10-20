package com.example.demo.Domain.Enums;

public enum EnumConditionRequirement {
    EQUAL(0),
    LESS_OR_EQUAL(1),
    GREATER_OR_EQUAL(2),
    LESS(3),
    GREATER(4);

    private int value;
    EnumConditionRequirement(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /** Whether the value the student gave satisfies this condition against the required value. */
    public boolean isSatisfied(double actual, double required) {
        int comparison = Double.compare(actual, required);
        return switch (this) {
            case EQUAL -> comparison == 0;
            case LESS_OR_EQUAL -> comparison <= 0;
            case GREATER_OR_EQUAL -> comparison >= 0;
            case LESS -> comparison < 0;
            case GREATER -> comparison > 0;
        };
    }

    public static EnumConditionRequirement fromValue(int value) {
        for (EnumConditionRequirement state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ConvocationState value: " + value);
    }
}
