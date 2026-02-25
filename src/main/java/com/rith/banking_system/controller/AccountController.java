package com.rith.banking_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rith.banking_system.dto.CreateAccountRequest;
import com.rith.banking_system.dto.TransactionRequest;
import com.rith.banking_system.dto.TransactionResponse;
import com.rith.banking_system.dto.TransferRequest;
import com.rith.banking_system.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/deposit")
    public String deposit(@Valid @RequestBody TransactionRequest request) {
        accountService.deposit(request.getAccountNumber(), request.getAmount());
        return "Deposit successful";
    }

    @PostMapping("/withdraw")
    public String withdraw(@Valid @RequestBody TransactionRequest request) {
        accountService.withdraw(request.getAccountNumber(), request.getAmount());
        return "Withdrawal successful";
    }

    @PostMapping("/transfer")
    public String transfer(@Valid @RequestBody TransferRequest request) {
        accountService.transfer(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount());
        return "Transfer successful";
    }

    @PostMapping("/create")
    public String createAccount(@Valid @RequestBody CreateAccountRequest request) {
        accountService.createAccount(
                request.getUserId(),
                request.getAccountNumber());
        return "Account created successfully";
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> getTransactions(
            @PathVariable String accountNumber) {

        return accountService.getTransactionHistory(accountNumber);
    }
}
