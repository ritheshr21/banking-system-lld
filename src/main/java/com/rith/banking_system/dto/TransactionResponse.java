package com.rith.banking_system.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private Double amount;
    private String type;
    private LocalDateTime timestamp;
    private String direction;
}
