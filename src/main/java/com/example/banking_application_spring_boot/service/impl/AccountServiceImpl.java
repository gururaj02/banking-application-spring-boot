package com.example.banking_application_spring_boot.service.impl;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.dto.PinRequestDto;
import com.example.banking_application_spring_boot.dto.TransactionDto;
import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Transaction;
import com.example.banking_application_spring_boot.entity.Users;
import com.example.banking_application_spring_boot.exception.*;
import com.example.banking_application_spring_boot.mapper.AccountMapper;
import com.example.banking_application_spring_boot.mapper.TransactionMapper;
import com.example.banking_application_spring_boot.repository.AccountRepository;
import com.example.banking_application_spring_boot.repository.TransactionRepository;
import com.example.banking_application_spring_boot.repository.UserDetailsRepository;
import com.example.banking_application_spring_boot.service.AccountService;
import com.example.banking_application_spring_boot.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, UserDetailsRepository userDetailsRepository, TransactionService transactionService, TransactionRepository transactionRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateAccountNumber() {

        return "ACC" + System.currentTimeMillis();
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

        Account account = user.getAccount();

        // Case 1: Account already active
        if (account != null && account.isActive()) {
            throw new AccountException("Account already exists");
        }

        // Case 2: Account exists but closed → Reactivate
        if (account != null && !account.isActive()) {

            if (createAccountRequest.initialDeposit() <= 0) {
                throw new InsufficientBalanceException("Initial deposit must be greater than zero");
            }

            account.setActive(true);
            account.setSecurityPin(passwordEncoder.encode(createAccountRequest.securityPin()));
            account.setAccountHolderName(createAccountRequest.accountHolderName());

            Account savedAccount = accountRepository.save(account);

            return AccountMapper.mapToAccountDto(savedAccount);
        }

        if (createAccountRequest.initialDeposit() <= 0) {
            throw new InsufficientBalanceException("Initial deposit must be greater than zero");
        }

        // Create account
        Account newAccount = new Account();
        newAccount.setAccountHolderName(createAccountRequest.accountHolderName());
        newAccount.setBalance(createAccountRequest.initialDeposit());
        newAccount.setAccountNumber(generateAccountNumber());
        newAccount.setSecurityPin(passwordEncoder.encode(createAccountRequest.securityPin()));
        newAccount.setActive(true);
        newAccount.setUser(user);

        Account savedAccount = accountRepository.save(newAccount);

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

        // Check Account is Active
        if (!account.isActive()) {
            throw new AccountException("Account is closed");
        }

        return AccountMapper.mapToAccountDto(account);
    }

    // Deposit Amount
    @Override
    @Transactional
    public AccountDto deposit(double amount, String securityPin) {

        if (amount <= 0) {
            throw new DepositOrWithdrawZeroRsException("Deposit amount must be greater than zero");
        }

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        // Check Account is Active
        if (!account.isActive()) {
            throw new AccountException("Account is closed");
        }

        // Validate PIN
        if (!passwordEncoder.matches(securityPin, account.getSecurityPin())) {
            throw new InvalidPinException("Invalid security PIN");
        }

        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account updatedAccount = accountRepository.save(account);

        // Saving transaction
        transactionService.recordDeposit(account, amount);

        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    // Withdraw Amount
    @Override
    @Transactional
    public AccountDto withdraw(double amount, String securityPin) {

        if (amount <= 0) {
            throw new DepositOrWithdrawZeroRsException("Withdraw amount must be greater than zero");
        }

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        // Check Account is Active
        if (!account.isActive()) {
            throw new AccountException("Account is closed");
        }

        // Validate PIN
        if (!passwordEncoder.matches(securityPin, account.getSecurityPin())) {
            throw new InvalidPinException("Invalid security PIN");
        }

        if(account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient Balance!");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account updatedAccount = accountRepository.save(account);

        // Saving transaction
        transactionService.recordWithdraw(account, amount);

        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    // Transfer Money
    @Override
    @Transactional
    public AccountDto transfer(String receiverAccountNumber, double amount, String securityPin) {

        if (amount <= 0) {
            throw new DepositOrWithdrawZeroRsException("Transfer amount must be greater than zero");
        }

        // Get logged-in user
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users senderUser = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("Sender Not Found"));

        // Get sender account
        Account senderAccount = accountRepository.findByUser(senderUser)
                .orElseThrow(() -> new AccountException("Sender Account Not Found"));

        // Check Account is Active
        if (!senderAccount.isActive()) {
            throw new AccountException("Account is closed");
        }

        // Validate PIN
        if (!passwordEncoder.matches(securityPin, senderAccount.getSecurityPin())) {
            throw new InvalidPinException("Invalid security PIN");
        }

        // Get receiver account using account number
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new AccountException("Receiver Account Not Found"));

        // Check Account is Active
        if (!receiverAccount.isActive()) {
            throw new AccountException("Receiver Account is closed");
        }

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

        // Saving transaction
        transactionService.recordTransfer(senderAccount, receiverAccount, amount);

        return AccountMapper.mapToAccountDto(senderAccount);
    }

    // Get All Transactions
    @Override
    public List<TransactionDto> getMyTransactions(String securityPin) {
        // 1 Get logged-in username
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        // 2 Get user
        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found"));

        // 3 Get account
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("Account Not Found"));

        // Check Account is Active
        if (!account.isActive()) {
            throw new AccountException("Account is closed");
        }

        // 4 Validate Pin
        if (!passwordEncoder.matches(securityPin, account.getSecurityPin())) {
            throw new InvalidPinException("Invalid security PIN");
        }

        // 5 Fetch transactions
        List<Transaction> transactions =
                transactionRepository.findBySenderAccountOrReceiverAccount(account, account);

        // 6 Convert to DTO
        return transactions.stream()
                .map((transaction) -> TransactionMapper.mapToTransactionDto(transaction))
                .toList();
    }

    @Override
    public List<AccountDto> getAllAccounts() {

        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map((account) -> AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(String securityPin) {

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        Users user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new AccountException("User Not Found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountException("Account Does Not Exists!!"));

        // Check Account is Active
        if (!account.isActive()) {
            throw new AccountException("Account is already closed");
        }

        if(!passwordEncoder.matches(securityPin, account.getSecurityPin())) {
            throw new InvalidPinException("Invalid security PIN");
        }

        if (!account.isActive()) {
            throw new RuntimeException("Account is closed");
        }

        if(account.getBalance() > 0) {
            throw new InsufficientBalanceException("Withdraw balance before deleting account");
        }

        account.setActive(false);
        accountRepository.save(account);
    }
}
