package com.rith.banking_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rith.banking_system.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
