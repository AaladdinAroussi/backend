package com.pfe.smsworkflow.Models;

public enum NotificationType {
    JOB_POSTED,
    JOB_APPROVED,
    JOB_REJECTED,
    JOB_PENDING,

    // Notifications liées aux candidatures
    NEW_APPLICATION,
    APPLICATION_RECEIVED,

    // Notifications de communication
    MESSAGE_RECEIVED,
    VERIFICATION,
    changePassword

    }
