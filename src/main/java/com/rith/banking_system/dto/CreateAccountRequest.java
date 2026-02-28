package com.rith.banking_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;
}
