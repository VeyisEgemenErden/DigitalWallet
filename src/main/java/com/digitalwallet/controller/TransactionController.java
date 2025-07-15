package com.digitalwallet.controller;

import com.digitalwallet.dto.ApproveRequest;
import com.digitalwallet.dto.TransactionResponse;
import com.digitalwallet.entity.Transaction;
import com.digitalwallet.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping("/wallets/{walletId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long walletId,
                                                                     HttpServletRequest httpRequest) {
        List<Transaction> transactions =
                transactionService.getTransactionsByWalletId(walletId, httpRequest);
        List<TransactionResponse> responseList = transactions.stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/transactions/approve")
    public ResponseEntity<String> approveOrDeny(@RequestBody @Valid ApproveRequest request) {
        transactionService.approveOrDenyTransaction(request);
        return ResponseEntity.ok("Transaction updated successfully.");
    }
}
