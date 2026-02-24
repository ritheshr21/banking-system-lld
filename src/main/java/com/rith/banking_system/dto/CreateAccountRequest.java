package com.rith.banking_system.dto;

import lombok.Data;

@Data
public class CreateAccountRequest {

    private Long userId;
    private String accountNumber;
}
