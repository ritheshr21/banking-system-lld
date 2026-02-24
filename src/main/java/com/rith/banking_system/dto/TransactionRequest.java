package com.rith.banking_system.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String accountNumber;
    private Double amount;
}
