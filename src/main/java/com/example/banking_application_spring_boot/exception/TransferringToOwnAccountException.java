package com.example.banking_application_spring_boot.exception;

public class TransferringToOwnAccountException extends RuntimeException {

    public TransferringToOwnAccountException(String message) {
        super(message);
    }
}
