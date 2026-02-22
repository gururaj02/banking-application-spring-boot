package com.example.banking_application_spring_boot.service;

import com.example.banking_application_spring_boot.dto.AccountDto;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);
}
