package com.example.banking_application_spring_boot.dto;

import java.time.LocalDateTime;

public record TransactionDto(
        Long id,
        String type,
        double amount,
        Long senderAccountId,
        Long receiverAccountId,
        LocalDateTime transactionDate,
        String status
) {
}
