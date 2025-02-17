package com.pfe.smsworkflow.Models;

public enum CodeStatus {
    NOT_SENT(0),  // Code not sent
    SENT(1),      // Code sent
    RESENT(2);    // Code resent

    private final int value;

    CodeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CodeStatus fromValue(int value) {
        for (CodeStatus status : CodeStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code status value: " + value);
    }
}