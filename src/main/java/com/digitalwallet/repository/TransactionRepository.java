package com.digitalwallet.repository;

import com.digitalwallet.entity.Transaction;
import com.digitalwallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction,Long>{
    List<Transaction> findByWallet(Wallet wallet);
    List<Transaction> findByWalletId(Long id);
}
