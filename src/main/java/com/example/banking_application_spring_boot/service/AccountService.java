package com.example.banking_application_spring_boot.service;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.dto.TransactionDto;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(CreateAccountRequest createAccountRequest);

    AccountDto deposit(double amount);

    AccountDto withdraw(double amount);

    AccountDto getMyAccount();

    AccountDto transfer(Long receiverAccountNumber, double amount);

    List<TransactionDto> getMyTransactions();

    List<AccountDto> getAllAccounts();

    // TODO: Dele account
    void deleteAccount(Long id);
}
