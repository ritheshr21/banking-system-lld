package com.rith.banking_system.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rith.banking_system.dto.CreateAccountRequest;
import com.rith.banking_system.dto.TransactionRequest;
import com.rith.banking_system.dto.TransferRequest;
import com.rith.banking_system.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/deposit")
    public String deposit(@RequestBody TransactionRequest request) {
        accountService.deposit(request.getAccountNumber(), request.getAmount());
        return "Deposit successful";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionRequest request) {
        accountService.withdraw(request.getAccountNumber(), request.getAmount());
        return "Withdrawal successful";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        accountService.transfer(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount());
        return "Transfer successful";
    }

    @PostMapping("/create")
    public String createAccount(@RequestBody CreateAccountRequest request) {
        accountService.createAccount(
                request.getUserId(),
                request.getAccountNumber());
        return "Account created successfully";
    }
}
