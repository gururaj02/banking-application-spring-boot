package com.example.banking_application_spring_boot.service.impl;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Users;
import com.example.banking_application_spring_boot.exception.AccountException;
import com.example.banking_application_spring_boot.exception.InsufficientBalanceException;
import com.example.banking_application_spring_boot.exception.TransferringToOwnAccountException;
import com.example.banking_application_spring_boot.mapper.AccountMapper;
import com.example.banking_application_spring_boot.repository.AccountRepository;
import com.example.banking_application_spring_boot.repository.UserDetailsRepository;
import com.example.banking_application_spring_boot.service.AccountService;
import jakarta.transaction.Transactional;
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

        // Get logged-in username
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        // Get user from DB
        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if account already exists
        if (user.getAccount() != null) {
            throw new RuntimeException("Account already exists");
        }
//        if (accountRepository.existsByUser(user)) {
//            throw new AccountException("Account already exists");
//        }

        // Create account
        Account account = new Account();
        account.setAccountHolderName(createAccountRequest.accountHolderName());
        account.setBalance(createAccountRequest.initialDeposit());
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);

        // Map to DTO
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    // Get Current user account
    @Override
    public AccountDto getMyAccount() {
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found!"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("User Not Found!"));

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        return AccountMapper.mapToAccountDto(account);
    }

    // Deposit Amount
    @Override
    @Transactional
    public AccountDto deposit(double amount) {

        if (amount <= 0) {
            throw new AccountException("Deposit amount must be greater than zero");
        }

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        double total = account.getBalance() + amount;
        account.setBalance(total);

        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    // Withdraw Amount
    @Override
    @Transactional
    public AccountDto withdraw(double amount) {

        if (amount <= 0) {
            throw new AccountException("Withdraw amount must be greater than zero");
        }

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        if(account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient Balance!");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    // Transfer Money
    @Override
    @Transactional
    public AccountDto transfer(Long receiverAccountNumber, double amount) {

        if (amount <= 0) {
            throw new AccountException("Transfer amount must be greater than zero");
        }

        // Sender
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users senderUser = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("Sender Not Found"));

        Account senderAccount = accountRepository.findByUser(senderUser)
                .orElseThrow(() -> new AccountException("Sender Account Not Found"));

        // Receiver
        Users receiverUser = userDetailsRepository.findById(receiverAccountNumber)
                .orElseThrow(() -> new AccountException("Receiver Not Found"));

        Account receiverAccount = accountRepository.findByUser(receiverUser)
                .orElseThrow(() -> new AccountException("Receiver Account Not Found"));

        // Prevent self transfer
        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new TransferringToOwnAccountException("Cannot Transfer To Your Own Account");
        }

        // Check balance
        if (senderAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient Balance For Transfer");
        }

        // Perform Transfer
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        // Save both
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        return AccountMapper.mapToAccountDto(senderAccount);
    }


    // TODO: Get all accounts for ADMIN only
    @Override
    public List<AccountDto> getAllAccounts() {

        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map((account) -> AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());
    }

    // TODO: Delete account
    @Override
    public void deleteAccount(Long id) {

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account Does Not Exists!!"));
        accountRepository.deleteById(id);
    }
}
