package com.digitalwallet.service;

import com.digitalwallet.dto.CreateWalletRequest;
import com.digitalwallet.dto.DepositRequest;
import com.digitalwallet.dto.WithdrawRequest;
import com.digitalwallet.entity.AppUser;
import com.digitalwallet.entity.Customer;
import com.digitalwallet.entity.Wallet;
import com.digitalwallet.enums.Currency;
import com.digitalwallet.enums.OppositePartyType;
import com.digitalwallet.enums.Status;
import com.digitalwallet.repository.CustomerRepository;
import com.digitalwallet.repository.TransactionRepository;
import com.digitalwallet.repository.WalletRepository;
import com.digitalwallet.security.JwtUtil;
import com.digitalwallet.security.SecurityUtilMock;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    private WalletService walletService;
    private WalletRepository walletRepository;
    private CustomerRepository customerRepository;
    private TransactionRepository transactionRepository;
    private JwtUtil jwtUtil;
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepository.class);
        customerRepository = mock(CustomerRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        jwtUtil = mock(JwtUtil.class);
        httpRequest = new MockHttpServletRequest();

        ((MockHttpServletRequest) httpRequest).addHeader("Authorization", "Bearer faketoken");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((MockHttpServletRequest) httpRequest));

        walletService = new WalletService(walletRepository, customerRepository, transactionRepository, jwtUtil);

        SecurityUtilMock.mockCurrentUserWithRole("CUSTOMER", 10L);
        when(jwtUtil.extractUserId("faketoken")).thenReturn(10L);
    }

    @AfterEach
    void tearDown() {
        SecurityUtilMock.clear();
    }

    @Test
    void shouldCreateWallet_whenValidCustomerAndUserOwnsIt() {
        AppUser user = new AppUser();
        user.setId(10L);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Ali");
        customer.setAppUser(user);

        CreateWalletRequest request = new CreateWalletRequest();
        request.setWalletName("Main");
        request.setCurrency(Currency.TRY);
        request.setCustomerId(1L);
        request.setActiveForShopping(true);
        request.setActiveForWithdraw(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(walletRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        var response = walletService.createWallet(request, httpRequest);

        assertThat(response.getWalletName()).isEqualTo("Main");
        assertThat(response.getCurrency()).isEqualTo(Currency.TRY);
    }

    @Test
    void shouldDeposit_whenUserOwnsWallet() {
        AppUser user = new AppUser();
        user.setId(10L);

        Customer customer = new Customer();
        customer.setAppUser(user);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setUsableBalance(BigDecimal.valueOf(100));
        wallet.setCustomer(customer);

        DepositRequest request = new DepositRequest();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setOppositeParty("TR123");
        request.setOppositePartyType(OppositePartyType.IBAN);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);

        var result = walletService.deposit(request, httpRequest);

        assertThat(result.getBalance()).isEqualByComparingTo("600");
        assertThat(result.getUsableBalance()).isEqualByComparingTo("600");
    }

    @Test
    void shouldWithdraw_whenAmountLessThanThreshold() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setOppositeParty("X");
        request.setOppositePartyType(OppositePartyType.IBAN);

        AppUser user = new AppUser();
        user.setId(10L);

        Customer customer = new Customer();
        customer.setAppUser(user);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(800));
        wallet.setActiveForWithdraw(true);
        wallet.setCustomer(customer);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.withdraw(request, httpRequest);

        assertThat(wallet.getBalance()).isEqualByComparingTo("500");
        assertThat(wallet.getUsableBalance()).isEqualByComparingTo("300");
        verify(transactionRepository).save(any());
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldWithdrawAsPending_whenAmountExceedsThreshold() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(2000));
        request.setOppositeParty("X");
        request.setOppositePartyType(OppositePartyType.IBAN);

        AppUser user = new AppUser();
        user.setId(10L);

        Customer customer = new Customer();
        customer.setAppUser(user);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(3000));
        wallet.setUsableBalance(BigDecimal.valueOf(2500));
        wallet.setActiveForWithdraw(true);
        wallet.setCustomer(customer);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.withdraw(request, httpRequest);

        assertThat(wallet.getBalance()).isEqualByComparingTo("3000");
        assertThat(wallet.getUsableBalance()).isEqualByComparingTo("500");
        verify(transactionRepository).save(argThat(tx -> tx.getStatus() == Status.PENDING));
    }

    @Test
    void shouldThrow_whenWalletNotActiveForWithdraw() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(100));
        request.setOppositeParty("X");
        request.setOppositePartyType(OppositePartyType.IBAN);

        AppUser user = new AppUser();
        user.setId(10L);

        Customer customer = new Customer();
        customer.setAppUser(user);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setActiveForWithdraw(false);
        wallet.setCustomer(customer);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                walletService.withdraw(request, httpRequest));

        assertThat(ex.getMessage()).contains("Withdraw not allowed for this wallet");
    }

    @Test
    void shouldThrow_whenInsufficientUsableBalance() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(100));
        request.setOppositeParty("X");
        request.setOppositePartyType(OppositePartyType.IBAN);

        AppUser user = new AppUser();
        user.setId(10L);

        Customer customer = new Customer();
        customer.setAppUser(user);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(500));
        wallet.setUsableBalance(BigDecimal.valueOf(50));
        wallet.setActiveForWithdraw(true);
        wallet.setCustomer(customer);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                walletService.withdraw(request, httpRequest));

        assertThat(ex.getMessage()).contains("Insufficient usable balance");
        verify(transactionRepository, never()).save(any());
    }
}
