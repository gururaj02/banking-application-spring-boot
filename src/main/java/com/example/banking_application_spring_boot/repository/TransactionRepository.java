package com.example.banking_application_spring_boot.repository;

import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderAccountOrReceiverAccount(Account sender, Account receiver);

}
