package com.example.banking_application_spring_boot.repository;

import com.example.banking_application_spring_boot.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
