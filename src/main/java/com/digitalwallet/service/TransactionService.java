package com.digitalwallet.service;

import com.digitalwallet.dto.ApproveRequest;
import com.digitalwallet.entity.Transaction;
import com.digitalwallet.entity.Wallet;
import com.digitalwallet.enums.Status;
import com.digitalwallet.enums.TransactionType;
import com.digitalwallet.repository.TransactionRepository;
import com.digitalwallet.repository.WalletRepository;
import com.digitalwallet.security.JwtUtil;
import com.digitalwallet.security.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Service

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final JwtUtil jwtUtil;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, JwtUtil jwtUtil) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<Transaction> getTransactionsByWalletId(Long walletId, HttpServletRequest httpRequest) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));


        if (SecurityUtil.isCurrentUserInRole("CUSTOMER")) {
            Long currentUserId = SecurityUtil.getCurrentUserId(httpRequest, jwtUtil);
            Long ownerUserId = wallet.getCustomer().getAppUser().getId();

            if (!currentUserId.equals(ownerUserId)) {
                throw new AccessDeniedException("You are not allowed to view transactions of this wallet.");

            }
        }

        return transactionRepository.findByWalletId(walletId);
    }

    @Transactional
    public void approveOrDenyTransaction(ApproveRequest request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transaction.getStatus() != Status.PENDING) {
            throw new RuntimeException("Only PENDING transactions can be updated.");
        }

        Wallet wallet = transaction.getWallet();
        BigDecimal amount = transaction.getAmount();

        if (request.getStatus() == Status.APPROVED) {
            if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
                wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
            } else if (transaction.getTransactionType() == TransactionType.WITHDRAW) {
                wallet.setBalance(wallet.getBalance().subtract(amount));
            }
        } else if (request.getStatus() == Status.DENIED) {
            if (transaction.getTransactionType() == TransactionType.WITHDRAW) {
                wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
            }
        }

        transaction.setStatus(request.getStatus());
        walletRepository.save(wallet);
        transactionRepository.save(transaction);
    }


}
