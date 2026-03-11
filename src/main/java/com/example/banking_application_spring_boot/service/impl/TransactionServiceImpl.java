package com.example.banking_application_spring_boot.service.impl;

import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Transaction;
import com.example.banking_application_spring_boot.entity.enums.TransactionStatus;
import com.example.banking_application_spring_boot.entity.enums.TransactionType;
import com.example.banking_application_spring_boot.repository.TransactionRepository;
import com.example.banking_application_spring_boot.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void recordDeposit(Account receiver, double amount) {

        // Saving transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setReceiverAccount(receiver);
        transaction.setDescription("Money deposited");

        transactionRepository.save(transaction);
    }

    @Override
    public void recordWithdraw(Account sender, double amount) {

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSenderAccount(sender);
        transaction.setDescription("Money Withdrawn");

        transactionRepository.save(transaction);
    }

    @Override
    public void recordTransfer(Account sender, Account receiver, double amount) {

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setReceiverAccount(receiver);
        transaction.setSenderAccount(sender);
        transaction.setDescription("Money Transferred");

        transactionRepository.save(transaction);
    }
}
