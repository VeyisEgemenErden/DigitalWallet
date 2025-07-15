package com.digitalwallet.dto;

import com.digitalwallet.enums.OppositePartyType;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class WithdrawRequest {

    @NotNull
    private Long walletId;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull
    private String oppositeParty;

    @NotNull
    @Schema(description = "Opposite party type", example = "PAYMENT")
    private OppositePartyType oppositePartyType;


    public WithdrawRequest() {
    }

    public WithdrawRequest(Long walletId, BigDecimal amount, String oppositeParty, OppositePartyType oppositePartyType) {
        this.walletId = walletId;
        this.amount = amount;
        this.oppositeParty = oppositeParty;
        this.oppositePartyType = oppositePartyType;
    }


    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getOppositeParty() {
        return oppositeParty;
    }

    public OppositePartyType getOppositePartyType() {
        return oppositePartyType;
    }


    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setOppositeParty(String oppositeParty) {
        this.oppositeParty = oppositeParty;
    }

    public void setOppositePartyType(OppositePartyType oppositePartyType) {
        this.oppositePartyType = oppositePartyType;
    }
}
