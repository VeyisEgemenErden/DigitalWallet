package com.digitalwallet.repository;

import com.digitalwallet.entity.Wallet;
import com.digitalwallet.entity.Customer;
import com.digitalwallet.enums.Currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface WalletRepository extends JpaRepository<Wallet,Long> {

    List<Wallet> findByCustomer(Customer customer);

    List<Wallet> findByCustomerAndCurrency(Customer customer,Currency currency);

}
