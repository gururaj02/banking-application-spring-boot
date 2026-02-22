package com.example.banking_application_spring_boot.exception;

public class AccountException extends RuntimeException {

    public AccountException(String message) {
        super(message);
    }
}
