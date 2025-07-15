package com.digitalwallet.controller;

import com.digitalwallet.dto.*;

import com.digitalwallet.entity.Wallet;

import com.digitalwallet.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/wallets")

public class WalletController {
    private final WalletService walletService;
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(
            @RequestBody @Valid CreateWalletRequest request,
            HttpServletRequest httpRequest) {
        WalletResponse response = walletService.createWallet(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(@Valid @RequestBody DepositRequest request,
                                                  HttpServletRequest httpRequest) {
        WalletResponse response = walletService.deposit(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(@Valid @RequestBody WithdrawRequest request,
                                                   HttpServletRequest httpRequest) {
        Wallet wallet = walletService.withdraw(request, httpRequest);
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }


}
