package com.example.banking_application_spring_boot.service;

import com.example.banking_application_spring_boot.entity.Account;

public interface TransactionService {

    void recordDeposit(Account receiver, double amount);

    void recordWithdraw(Account sender, double amount);

    void recordTransfer(Account sender, Account receiver, double amount);
}
