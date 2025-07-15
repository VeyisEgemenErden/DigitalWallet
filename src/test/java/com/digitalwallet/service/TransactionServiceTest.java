package com.digitalwallet.service;

import com.digitalwallet.dto.ApproveRequest;
import com.digitalwallet.entity.AppUser;
import com.digitalwallet.entity.Customer;
import com.digitalwallet.entity.Transaction;
import com.digitalwallet.entity.Wallet;
import com.digitalwallet.enums.Status;
import com.digitalwallet.enums.TransactionType;
import com.digitalwallet.repository.TransactionRepository;
import com.digitalwallet.repository.WalletRepository;
import com.digitalwallet.security.JwtUtil;
import com.digitalwallet.security.SecurityUtilMock;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionService transactionService;
    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;
    private JwtUtil jwtUtil;
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        walletRepository = mock(WalletRepository.class);
        jwtUtil = mock(JwtUtil.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        this.httpRequest = request;

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        transactionService = new TransactionService(transactionRepository, walletRepository, jwtUtil);

        SecurityUtilMock.mockCurrentUserWithRole("CUSTOMER", 10L);

        when(jwtUtil.extractUserId("faketoken")).thenReturn(10L);
    }



    @Test
    void shouldReturnTransactions_whenUserIsOwner() {
        AppUser appUser = new AppUser();
        appUser.setId(10L);

        Customer customer = new Customer();
        customer.setAppUser(appUser);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setCustomer(customer);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(jwtUtil.extractUserId(any())).thenReturn(10L);
        when(transactionRepository.findByWalletId(1L)).thenReturn(List.of(new Transaction()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        List<Transaction> result = transactionService.getTransactionsByWalletId(1L, request);

        assertThat(result).hasSize(1);
    }


    @Test
    void shouldThrowException_whenUserIsNotOwner() {
        AppUser appUser = new AppUser();
        appUser.setId(5L);

        Customer customer = new Customer();
        customer.setAppUser(appUser);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setCustomer(customer);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(jwtUtil.extractUserId(any())).thenReturn(10L);

        assertThrows(Exception.class, () ->
                transactionService.getTransactionsByWalletId(1L, httpRequest));
    }

    @Test
    void shouldApproveDepositTransaction() {
        Wallet wallet = new Wallet();
        wallet.setUsableBalance(BigDecimal.valueOf(100));

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(200));
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(Status.PENDING);

        ApproveRequest request = new ApproveRequest();
        request.setTransactionId(1L);
        request.setStatus(Status.APPROVED);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.approveOrDenyTransaction(request);

        assertThat(wallet.getUsableBalance()).isEqualByComparingTo("300");
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldApproveWithdrawTransaction() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(500));

        Transaction transaction = new Transaction();
        transaction.setId(2L);
        transaction.setAmount(BigDecimal.valueOf(200));
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(Status.PENDING);

        ApproveRequest request = new ApproveRequest();
        request.setTransactionId(2L);
        request.setStatus(Status.APPROVED);

        when(transactionRepository.findById(2L)).thenReturn(Optional.of(transaction));

        transactionService.approveOrDenyTransaction(request);

        assertThat(wallet.getBalance()).isEqualByComparingTo("300");
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldDenyWithdrawTransaction() {
        Wallet wallet = new Wallet();
        wallet.setUsableBalance(BigDecimal.valueOf(100));

        Transaction transaction = new Transaction();
        transaction.setId(3L);
        transaction.setAmount(BigDecimal.valueOf(50));
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(Status.PENDING);

        ApproveRequest request = new ApproveRequest();
        request.setTransactionId(3L);
        request.setStatus(Status.DENIED);

        when(transactionRepository.findById(3L)).thenReturn(Optional.of(transaction));

        transactionService.approveOrDenyTransaction(request);

        assertThat(wallet.getUsableBalance()).isEqualByComparingTo("150");
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldThrowException_whenTransactionNotPending() {
        Transaction transaction = new Transaction();
        transaction.setId(4L);
        transaction.setStatus(Status.APPROVED);

        ApproveRequest request = new ApproveRequest();
        request.setTransactionId(4L);
        request.setStatus(Status.DENIED);

        when(transactionRepository.findById(4L)).thenReturn(Optional.of(transaction));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                transactionService.approveOrDenyTransaction(request));

        assertThat(ex.getMessage()).contains("Only PENDING transactions");
    }

    @Test
    void shouldThrowException_whenTransactionNotFound() {
        ApproveRequest request = new ApproveRequest();
        request.setTransactionId(5L);
        request.setStatus(Status.APPROVED);

        when(transactionRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                transactionService.approveOrDenyTransaction(request));

        assertThat(ex.getMessage()).contains("Transaction not found");
    }
}
