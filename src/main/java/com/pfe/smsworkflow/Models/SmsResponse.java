package com.pfe.smsworkflow.Models;

import java.util.Date;

public class SmsResponse {
    private boolean success;
    private Long smsId; // For SMS ID
    private String dlr; // For DLR
    private Date dateDlr; // For DLR date

    // Constructor for success, smsId, dlr, and dateDlr
    public SmsResponse(boolean success, Long smsId, String dlr, Date dateDlr) {
        this.success = success;
        this.smsId = smsId;
        this.dlr = dlr;
        this.dateDlr = dateDlr;
    }

    // Constructor for success and smsId only
    public SmsResponse(boolean success, Long smsId) {
        this.success = success;
        this.smsId = smsId;
        this.dlr = null; // Default to null if DLR is not provided
        this.dateDlr = null; // Default to null if DLR date is not provided
    }

    public boolean isSuccess() {
        return success;
    }

    public Long getSmsId() {
        return smsId;
    }

    public String getDlr() {
        return dlr;
    }

    public Date getDateDlr() {
        return dateDlr;
    }
}