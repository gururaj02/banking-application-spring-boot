package com.example.banking_application_spring_boot.service;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(CreateAccountRequest createAccountRequest);

    AccountDto getAccountById(Long id);

    AccountDto deposit(double amount);

    AccountDto withdraw(double amount);

    List<AccountDto> getAllAccounts();

    void deleteAccount(Long id);

    AccountDto getMyAccount();
}
