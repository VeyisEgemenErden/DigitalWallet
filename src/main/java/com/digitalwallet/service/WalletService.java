package com.digitalwallet.service;

import com.digitalwallet.dto.CreateWalletRequest;
import com.digitalwallet.dto.WalletResponse;
import com.digitalwallet.dto.DepositRequest;
import com.digitalwallet.dto.WithdrawRequest;
import com.digitalwallet.entity.Customer;
import com.digitalwallet.entity.Wallet;
import com.digitalwallet.enums.Status;
import com.digitalwallet.enums.TransactionType;
import com.digitalwallet.repository.CustomerRepository;
import com.digitalwallet.repository.TransactionRepository;
import com.digitalwallet.repository.WalletRepository;
import com.digitalwallet.security.JwtUtil;
import com.digitalwallet.security.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.digitalwallet.entity.Transaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;

    public WalletService(WalletRepository walletRepository, CustomerRepository customerRepository,
                         TransactionRepository transactionRepository, JwtUtil jwtUtil) {
        this.walletRepository = walletRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.jwtUtil = jwtUtil;
    }

    public WalletResponse createWallet(CreateWalletRequest request, HttpServletRequest httpRequest) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (SecurityUtil.isCurrentUserInRole("CUSTOMER")) {
            Long currentUserId = SecurityUtil.getCurrentUserId(httpRequest, jwtUtil);
            if (!customer.getAppUser().getId().equals(currentUserId)) {
                throw new AccessDeniedException("You can only create wallets for yourself");
            }
        }

        Wallet wallet = new Wallet();
        wallet.setWalletName(request.getWalletName());
        wallet.setCurrency(request.getCurrency());
        wallet.setActiveForShopping(request.isActiveForShopping());
        wallet.setActiveForWithdraw(request.isActiveForWithdraw());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);
        wallet.setCustomer(customer);

        Wallet saved = walletRepository.save(wallet);
        return WalletResponse.from(saved);
    }

    @Transactional
    public WalletResponse deposit(DepositRequest request, HttpServletRequest httpRequest) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        if (SecurityUtil.isCurrentUserInRole("CUSTOMER")) {
            Long currentUserId = SecurityUtil.getCurrentUserId(httpRequest, jwtUtil);
            Long ownerUserId = wallet.getCustomer().getAppUser().getId();
            if (!currentUserId.equals(ownerUserId)) {
                throw new AccessDeniedException("You can only deposit to your own wallet");
            }
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0 ? Status.PENDING : Status.APPROVED);
        transaction.setOppositeParty(request.getOppositeParty());
        transaction.setOppositePartyType(request.getOppositePartyType());

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        if (transaction.getStatus() == Status.APPROVED) {
            wallet.setUsableBalance(wallet.getUsableBalance().add(request.getAmount()));
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);

        return WalletResponse.from(wallet);
    }

    @Transactional
    public Wallet withdraw(WithdrawRequest request, HttpServletRequest httpRequest) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!wallet.isActiveForWithdraw()) {
            throw new RuntimeException("Withdraw not allowed for this wallet.");
        }

        if (SecurityUtil.isCurrentUserInRole("CUSTOMER")) {
            Long currentUserId = SecurityUtil.getCurrentUserId(httpRequest, jwtUtil);
            Long ownerUserId = wallet.getCustomer().getAppUser().getId();
            if (!currentUserId.equals(ownerUserId)) {
                throw new AccessDeniedException("You can only withdraw from your own wallet");
            }
        }

        if (wallet.getUsableBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient usable balance.");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setOppositeParty(request.getOppositeParty());
        transaction.setOppositePartyType(request.getOppositePartyType());

        if (request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            transaction.setStatus(Status.PENDING);
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(request.getAmount()));
        } else {
            transaction.setStatus(Status.APPROVED);
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(request.getAmount()));
        }

        transactionRepository.save(transaction);
        return walletRepository.save(wallet);
    }
}
