package com.digitalwallet.repository;

import com.digitalwallet.entity.Customer;
import  org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CustomerRepository extends  JpaRepository<Customer,Long>{
    Optional<Customer> findByTCKN(String TCKN);
    Optional<Customer> findByAppUserId(Long appUserId);


}
