package com.example.banking_application_spring_boot.service.impl;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Users;
import com.example.banking_application_spring_boot.exception.AccountException;
import com.example.banking_application_spring_boot.exception.InsufficientBalanceException;
import com.example.banking_application_spring_boot.mapper.AccountMapper;
import com.example.banking_application_spring_boot.repository.AccountRepository;
import com.example.banking_application_spring_boot.repository.UserDetailsRepository;
import com.example.banking_application_spring_boot.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private UserDetailsRepository userDetailsRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              UserDetailsRepository userDetailsRepository) {
        this.accountRepository = accountRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    // Create Account
    @Override
    public AccountDto createAccount(CreateAccountRequest createAccountRequest) {

        // 1 Get logged-in username
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        // 2 Get user from DB
        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3 Check if account already exists
        if (user.getAccount() != null) {
            throw new RuntimeException("Account already exists");
        }

        // 4 Create account
        Account account = new Account();
        account.setAccountHolderName(createAccountRequest.accountHolderName());
        account.setBalance(createAccountRequest.initialDeposit());
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);

        // 5 Map to DTO
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account Does Not Exists!!"));
        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        if(account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient Balance!");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {

        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map((account) -> AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account Does Not Exists!!"));
        accountRepository.deleteById(id);
    }
}
