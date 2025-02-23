package com.pfe.smsworkflow.Security.Services;

public class RecordNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecordNotFoundException(String message) {
        super(message);
    }

}