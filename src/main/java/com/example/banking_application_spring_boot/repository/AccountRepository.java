package com.example.banking_application_spring_boot.repository;

import com.example.banking_application_spring_boot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
