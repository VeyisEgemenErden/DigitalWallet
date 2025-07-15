package com.digitalwallet.dto;

import com.digitalwallet.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Create Wallet Request")
public class CreateWalletRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private String walletName;

    @NotNull
    @Schema(description = "Wallet currency", example = "USD")
    private Currency currency;

    private boolean activeForShopping;

    private boolean activeForWithdraw;


    public CreateWalletRequest() {
    }

    public CreateWalletRequest(Long customerId, String walletName, Currency currency,
                               boolean activeForShopping, boolean activeForWithdraw) {
        this.customerId = customerId;
        this.walletName = walletName;
        this.currency = currency;
        this.activeForShopping = activeForShopping;
        this.activeForWithdraw = activeForWithdraw;
    }


    public Long getCustomerId() {
        return customerId;
    }

    public String getWalletName() {
        return walletName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isActiveForShopping() {
        return activeForShopping;
    }

    public boolean isActiveForWithdraw() {
        return activeForWithdraw;
    }


    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setActiveForShopping(boolean activeForShopping) {
        this.activeForShopping = activeForShopping;
    }

    public void setActiveForWithdraw(boolean activeForWithdraw) {
        this.activeForWithdraw = activeForWithdraw;
    }
}
