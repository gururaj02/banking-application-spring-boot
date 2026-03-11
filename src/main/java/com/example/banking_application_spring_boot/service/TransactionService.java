package com.example.banking_application_spring_boot.service;

import com.example.banking_application_spring_boot.dto.TransactionDto;
import com.example.banking_application_spring_boot.entity.Account;

import java.util.List;

public interface TransactionService {

    void recordDeposit(Account receiver, double amount);

    void recordWithdraw(Account sender, double amount);

    void recordTransfer(Account sender, Account receiver, double amount);
}
