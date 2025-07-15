package com.digitalwallet.dto;

import com.digitalwallet.entity.Wallet;
import com.digitalwallet.enums.Currency;

import java.math.BigDecimal;

public class WalletResponse {

    private Long id;
    private String walletName;
    private Currency currency;
    private boolean activeForShopping;
    private boolean activeForWithdraw;
    private BigDecimal balance;
    private BigDecimal usableBalance;
    private Long customerId;


    public WalletResponse() {
    }

    public WalletResponse(Long id, String walletName, Currency currency,
                          boolean activeForShopping, boolean activeForWithdraw,
                          BigDecimal balance, BigDecimal usableBalance, Long customerId) {
        this.id = id;
        this.walletName = walletName;
        this.currency = currency;
        this.activeForShopping = activeForShopping;
        this.activeForWithdraw = activeForWithdraw;
        this.balance = balance;
        this.usableBalance = usableBalance;
        this.customerId = customerId;
    }


    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getWalletName(),
                wallet.getCurrency(),
                wallet.isActiveForShopping(),
                wallet.isActiveForWithdraw(),
                wallet.getBalance(),
                wallet.getUsableBalance(),
                wallet.getCustomer().getId()
        );
    }


    public Long getId() {
        return id;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getUsableBalance() {
        return usableBalance;
    }

    public Long getCustomerId() {
        return customerId;
    }


    public void setId(Long id) {
        this.id = id;
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

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUsableBalance(BigDecimal usableBalance) {
        this.usableBalance = usableBalance;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
