package com.example.banking_application_spring_boot.exception;

public class DepositOrWithdrawZeroRsException extends RuntimeException {

    public DepositOrWithdrawZeroRsException(String message) {
        super(message);
    }
}
