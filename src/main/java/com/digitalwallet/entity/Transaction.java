package com.digitalwallet.entity;

import com.digitalwallet.enums.OppositePartyType;
import com.digitalwallet.enums.Status;
import com.digitalwallet.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private OppositePartyType oppositePartyType;

    private String oppositeParty;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;


    public Transaction() {
    }

    public Transaction(long id, BigDecimal amount, TransactionType transactionType,
                       OppositePartyType oppositePartyType, String oppositeParty,
                       Status status, Wallet wallet) {
        this.id = id;
        this.amount = amount;
        this.transactionType = transactionType;
        this.oppositePartyType = oppositePartyType;
        this.oppositeParty = oppositeParty;
        this.status = status;
        this.wallet = wallet;
    }


    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
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

    public Wallet getWallet() {
        return wallet;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
