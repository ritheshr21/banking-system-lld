package com.rith.banking_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rith.banking_system.entity.Account;
import com.rith.banking_system.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
}
