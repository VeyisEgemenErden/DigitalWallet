package com.digitalwallet.controller;


import com.digitalwallet.entity.Wallet;

import com.digitalwallet.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")

public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }
    @GetMapping("/{customerId}/wallets")
    public ResponseEntity<List<Wallet>> getCustomerWallets(@PathVariable Long customerId,
                                                           HttpServletRequest httpRequest) {
        List<Wallet> wallets = customerService.getCustomerWallets(customerId, httpRequest);
        return ResponseEntity.ok(wallets);
    }
}
