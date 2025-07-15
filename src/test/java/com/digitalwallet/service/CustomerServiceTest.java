package com.digitalwallet.service;

import com.digitalwallet.entity.AppUser;
import com.digitalwallet.entity.Customer;
import com.digitalwallet.entity.Wallet;
import com.digitalwallet.repository.CustomerRepository;
import com.digitalwallet.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private JwtUtil jwtUtil;
    private MockHttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        jwtUtil = mock(JwtUtil.class);

        AppUser principal = new AppUser();
        principal.setId(10L);
        principal.setUsername("testUser");
        principal.setRole("CUSTOMER");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer faketoken");

        when(jwtUtil.extractUserId("faketoken")).thenReturn(10L);

        customerService = new CustomerService(customerRepository, jwtUtil);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnWallets_whenCustomerAccessingOwnWallets() {
        Long customerId = 5L;

        AppUser user = new AppUser();
        user.setId(10L);

        Wallet wallet = new Wallet();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setAppUser(user);
        customer.setWallets(Collections.singletonList(wallet));

        when(customerRepository.findByAppUserId(10L)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        var wallets = customerService.getCustomerWallets(customerId, httpRequest);

        assertThat(wallets).hasSize(1);
    }


    @Test
    void shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findByAppUserId(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                customerService.getCustomerWallets(1L, httpRequest));
    }
}
