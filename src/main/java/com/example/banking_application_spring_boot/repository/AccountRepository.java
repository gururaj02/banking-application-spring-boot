package com.example.banking_application_spring_boot.repository;

import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

//    boolean existsByUser(Users user);

    Optional<Account> findByUser(Users user);

}
