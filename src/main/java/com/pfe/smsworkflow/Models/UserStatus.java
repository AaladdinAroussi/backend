package com.pfe.smsworkflow.Models;

public enum UserStatus {
    INACTIVE(0),  // Non actif
    ACTIVE(1),    // Actif
    BLOCKED(2);   // Bloqué

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Valeur de statut invalide : " + value);
    }
}

