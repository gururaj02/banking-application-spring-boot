package com.example.banking_application_spring_boot.service;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.dto.PinRequestDto;
import com.example.banking_application_spring_boot.dto.TransactionDto;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(CreateAccountRequest createAccountRequest);

    AccountDto deposit(double amount, String securityPin);

    AccountDto withdraw(double amount, String securityPin);

    AccountDto getMyAccount();

    AccountDto transfer(String receiverAccountNumber, double amount, String securityPin);

    List<TransactionDto> getMyTransactions(String securityPin);

    List<AccountDto> getAllAccounts();



    // TODO: Dele account
    void deleteAccount(Long id);
}
