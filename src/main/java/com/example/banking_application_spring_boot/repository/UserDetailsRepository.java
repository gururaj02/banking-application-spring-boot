package com.example.banking_application_spring_boot.repository;

import com.example.banking_application_spring_boot.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);
}
