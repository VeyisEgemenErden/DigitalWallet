package com.digitalwallet.service;

import com.digitalwallet.entity.Customer;
import com.digitalwallet.entity.Wallet;
import com.digitalwallet.repository.CustomerRepository;
import com.digitalwallet.security.JwtUtil;
import com.digitalwallet.security.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CustomerService {
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;

    public CustomerService(CustomerRepository customerRepository, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<Wallet> getCustomerWallets(Long customerId, HttpServletRequest httpRequest) {
        if (SecurityUtil.isCurrentUserInRole("CUSTOMER")) {
            Long currentUserId = SecurityUtil.getCurrentUserId(httpRequest, jwtUtil);
            Customer customer = customerRepository.findByAppUserId(currentUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found for AppUser ID: " + currentUserId));

            Long currentUserCustomerId = customer.getId();
            if (!customerId.equals(currentUserCustomerId)) {
                throw new AccessDeniedException("You can only view your own wallets.");

            }
        }
        return customerRepository.findById(customerId)
                .map(Customer::getWallets)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }


}

