package com.digitalwallet.dto;

import com.digitalwallet.enums.Status;
import jakarta.validation.constraints.NotNull;

public class ApproveRequest {

    @NotNull
    private Long transactionId;

    @NotNull
    private Status status;


    public ApproveRequest() {
    }

    public ApproveRequest(Long transactionId, Status status) {
        this.transactionId = transactionId;
        this.status = status;
    }


    public Long getTransactionId() {
        return transactionId;
    }

    public Status getStatus() {
        return status;
    }


    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
