package com.example.banking_application_spring_boot.service.impl;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.mapper.AccountMapper;
import com.example.banking_application_spring_boot.repository.AccountRepository;
import com.example.banking_application_spring_boot.service.AccountService;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {

        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account); // save method returns th saved account

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account Does Not Exists!!"));

        return AccountMapper.mapToAccountDto(account);
    }
}
