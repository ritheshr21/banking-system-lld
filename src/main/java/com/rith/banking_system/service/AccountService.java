package com.rith.banking_system.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rith.banking_system.dto.TransactionResponse;
import com.rith.banking_system.entity.Account;
import com.rith.banking_system.entity.Transaction;
import com.rith.banking_system.entity.User;
import com.rith.banking_system.entity.enums.AccountStatus;
import com.rith.banking_system.entity.enums.TransactionDirection;
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
                .direction(TransactionDirection.CREDIT)
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
                .direction(TransactionDirection.DEBIT)
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

        Transaction senderTransaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.DEBIT)
                .timestamp(LocalDateTime.now())
                .account(sender)
                .build();

        Transaction receiverTransaction = Transaction.builder()
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.CREDIT)
                .timestamp(LocalDateTime.now())
                .account(receiver)
                .build();

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccount(account);

        return transactions.stream()
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .amount(tx.getAmount())
                        .type(tx.getType().name())
                        .direction(tx.getDirection().name())
                        .timestamp(tx.getTimestamp())
                        .build())
                .toList();
    }
}