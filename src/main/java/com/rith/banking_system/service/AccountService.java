package com.rith.banking_system.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rith.banking_system.entity.Account;
import com.rith.banking_system.entity.Transaction;
import com.rith.banking_system.entity.User;
import com.rith.banking_system.entity.enums.AccountStatus;
import com.rith.banking_system.entity.enums.TransactionType;
import com.rith.banking_system.exception.AccountFrozenException;
import com.rith.banking_system.exception.AccountNotFoundException;
import com.rith.banking_system.exception.DuplicateAccountException;
import com.rith.banking_system.exception.InsufficientBalanceException;
import com.rith.banking_system.exception.UserNotFoundException;
import com.rith.banking_system.repository.AccountRepository;
import com.rith.banking_system.repository.TransactionRepository;
import com.rith.banking_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public Account createAccount(Long userId, String accountNumber) {

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new DuplicateAccountException("Account number already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(0.0)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        return accountRepository.save(account);
    }

    public void deposit(String accountNumber, Double amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Account is frozen");
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(transaction);
    }

    public void withdraw(String accountNumber, Double amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Account is frozen");
        }

        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .timestamp(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, Double amount) {

        Account sender = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Sender not found"));

        Account receiver = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Receiver not found"));

        if (sender.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Sender account is frozen");
        }

        if (sender.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .timestamp(LocalDateTime.now())
                .account(sender)
                .build();

        transactionRepository.save(transaction);
    }
}