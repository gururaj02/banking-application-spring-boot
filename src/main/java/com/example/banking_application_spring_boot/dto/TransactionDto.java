package com.example.banking_application_spring_boot.dto;

import java.time.LocalDateTime;

public record TransactionDto(
        Long id,
        String senderAccountNumber,
        String receiverAccountNumber,
        double amount,
        String type,
        LocalDateTime transactionDate,
        String status
) {
}
