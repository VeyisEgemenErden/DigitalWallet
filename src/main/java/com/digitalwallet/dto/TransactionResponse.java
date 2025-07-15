package com.digitalwallet.dto;

import com.digitalwallet.entity.Transaction;
import com.digitalwallet.enums.OppositePartyType;
import com.digitalwallet.enums.Status;
import com.digitalwallet.enums.TransactionType;

import java.math.BigDecimal;

public class TransactionResponse {

    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private TransactionType type;
    private OppositePartyType oppositePartyType;
    private String oppositeParty;
    private Status status;


    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long walletId, BigDecimal amount,
                               TransactionType type, OppositePartyType oppositePartyType,
                               String oppositeParty, Status status) {
        this.id = id;
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.oppositePartyType = oppositePartyType;
        this.oppositeParty = oppositeParty;
        this.status = status;
    }


    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getOppositePartyType(),
                transaction.getOppositeParty(),
                transaction.getStatus()
        );
    }


    public Long getId() {
        return id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public OppositePartyType getOppositePartyType() {
        return oppositePartyType;
    }

    public String getOppositeParty() {
        return oppositeParty;
    }

    public Status getStatus() {
        return status;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setOppositePartyType(OppositePartyType oppositePartyType) {
        this.oppositePartyType = oppositePartyType;
    }

    public void setOppositeParty(String oppositeParty) {
        this.oppositeParty = oppositeParty;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
