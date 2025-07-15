package com.digitalwallet.entity;

import com.digitalwallet.enums.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private String walletName;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private boolean activeForShopping;

    private boolean activeForWithdraw;

    private BigDecimal balance = BigDecimal.valueOf(0.0);

    private BigDecimal usableBalance = BigDecimal.valueOf(0.0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    @Schema(hidden = true)
    private Customer customer;


    public Wallet() {
    }

    public Wallet(Long id, String walletName, Currency currency,
                  boolean activeForShopping, boolean activeForWithdraw,
                  BigDecimal balance, BigDecimal usableBalance, Customer customer) {
        this.id = id;
        this.walletName = walletName;
        this.currency = currency;
        this.activeForShopping = activeForShopping;
        this.activeForWithdraw = activeForWithdraw;
        this.balance = balance;
        this.usableBalance = usableBalance;
        this.customer = customer;
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

    public Customer getCustomer() {
        return customer;
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

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
