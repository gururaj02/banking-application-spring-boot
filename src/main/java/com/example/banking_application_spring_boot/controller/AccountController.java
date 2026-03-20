package com.example.banking_application_spring_boot.controller;

import com.example.banking_application_spring_boot.dto.*;
import com.example.banking_application_spring_boot.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;
    // Constructor injection
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    // TODO: Dockerize this project
    // TODO: Add tests - JUnit & Mockito
    // TODO: Add Loggers(AOP)
    // TODO: Try to add Kafka


    // Add Account REST API
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        AccountDto accountDto = accountService.createAccount(createAccountRequest);

        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    // Get Account REST API
    @GetMapping("/me")
    public ResponseEntity<AccountDto> getMyAccount() {
        AccountDto accountDto = accountService.getMyAccount();

        return ResponseEntity.ok(accountDto);
    }

    // Deposit REST API
    @PutMapping("/deposit")
    public ResponseEntity<AccountDto> deposit(@RequestBody DepositRequestDto depositRequest) {

        AccountDto accountDto = accountService.deposit(depositRequest.amount(), depositRequest.securityPin());

        return ResponseEntity.ok(accountDto);
    }

    // Withdraw REST API
    @PutMapping("/withdraw")
    public ResponseEntity<AccountDto> withdraw(@RequestBody WithdrawRequestDto withdrawRequest) {

        AccountDto accountDto = accountService.withdraw(withdrawRequest.amount(), withdrawRequest.securityPin());

        return ResponseEntity.ok(accountDto);
    }

    // Transfer Money
    @PutMapping("/transfer")
    public ResponseEntity<AccountDto> transfer(@RequestBody TransferRequestDto transferRequest) {

        AccountDto accountDto = accountService.transfer(transferRequest.receiverAccountNumber(), transferRequest.amount(), transferRequest.securityPin());

        return ResponseEntity.ok(accountDto);
    }

    // Get All Transactions REST API
    @PostMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@RequestBody PinRequestDto pinRequestDto) {

        List<TransactionDto> transactions = accountService.getMyTransactions(pinRequestDto.securityPin());

        return ResponseEntity.ok(transactions);
    }

    // Get All Accounts REST API (Only for ADMIN)
    @GetMapping
    @PreAuthorize("hasAuthority('ACCOUNT_WRITE')")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {

        List<AccountDto> accounts = accountService.getAllAccounts();

        return ResponseEntity.ok(accounts);
    }

    // Delete Account REST API
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@RequestBody PinRequestDto pinRequestDto) {

        accountService.deleteAccount(pinRequestDto.securityPin());

        return ResponseEntity.ok("Account Deleted Successfully!!");
    }

}
