package com.example.banking_application_spring_boot.mapper;

import com.example.banking_application_spring_boot.dto.TransactionDto;
import com.example.banking_application_spring_boot.entity.Transaction;

public class TransactionMapper {

    public static TransactionDto mapToTransactionDto(Transaction transaction) {

        Long senderId = transaction.getSenderAccount() != null
                ? transaction.getSenderAccount().getId()
                : null;

        Long receiverId = transaction.getReceiverAccount() != null
                ? transaction.getReceiverAccount().getId()
                : null;

        return new TransactionDto(
                transaction.getId(),
                transaction.getTransactionType().name(),
                transaction.getAmount(),
                senderId,
                receiverId,
                transaction.getTransactionDate(),
                transaction.getStatus().name()
        );
    }
}
