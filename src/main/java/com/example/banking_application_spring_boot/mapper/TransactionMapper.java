package com.example.banking_application_spring_boot.mapper;

import com.example.banking_application_spring_boot.dto.TransactionDto;
import com.example.banking_application_spring_boot.entity.Transaction;

public class TransactionMapper {

    public static TransactionDto mapToTransactionDto(Transaction transaction) {

        String senderAccountNumber =
                transaction.getSenderAccount() != null
                        ? transaction.getSenderAccount().getAccountNumber()
                        : null;

        String receiverAccountNumber =
                transaction.getReceiverAccount() != null
                        ? transaction.getReceiverAccount().getAccountNumber()
                        : null;

        return new TransactionDto(
                transaction.getId(),
                senderAccountNumber,
                receiverAccountNumber,
                transaction.getAmount(),
                transaction.getTransactionType().name(),
                transaction.getTransactionDate(),
                transaction.getStatus().name()
        );
    }
}
