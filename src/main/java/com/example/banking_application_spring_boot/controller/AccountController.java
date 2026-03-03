package com.example.banking_application_spring_boot.controller;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.dto.DepositRequestDto;
import com.example.banking_application_spring_boot.dto.WithdrawRequestDto;
import com.example.banking_application_spring_boot.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;
    // Constructor injection
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


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

        AccountDto accountDto = accountService.deposit(depositRequest.amount());

        return ResponseEntity.ok(accountDto);
    }

    // Withdraw REST API
    @PutMapping("/withdraw")
    public ResponseEntity<AccountDto> withdraw(@RequestBody WithdrawRequestDto withdrawRequest) {

        AccountDto accountDto = accountService.withdraw(withdrawRequest.amount());

        return ResponseEntity.ok(accountDto);
    }

//    // Get All Accounts REST API
//    @GetMapping
//    public ResponseEntity<List<AccountDto>> getAllAccounts() {
//
//        List<AccountDto> accounts = accountService.getAllAccounts();
//
//        return ResponseEntity.ok(accounts);
//    }
//
//    // Delete Account REST API
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
//
//        accountService.deleteAccount(id);
//
//        return ResponseEntity.ok("Account Deleted Successfully!!");
//    }

}
