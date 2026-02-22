package com.example.banking_application_spring_boot.mapper;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.entity.Account;

public class AccountMapper {

    public static Account mapToAccount(AccountDto accountDto) {

//        Account account = new Account(
//                accountDto.getId(),
//                accountDto.getAccountHolderName(),
//                accountDto.getBalance()
//        );

        // Using Record class
        Account account = new Account(
                accountDto.id(),
                accountDto.accountHolderName(),
                accountDto.balance()
        );

        return account;
    }

    public static AccountDto mapToAccountDto(Account account) {

        return new AccountDto(
                account.getId(),
                account.getAccountHolderName(),
                account.getBalance()
        );
    }
}
