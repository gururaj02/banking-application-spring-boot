package com.example.banking_application_spring_boot.exception;

public class InvalidPinException extends RuntimeException {

    public InvalidPinException(String message) {
        super(message);
    }
}
