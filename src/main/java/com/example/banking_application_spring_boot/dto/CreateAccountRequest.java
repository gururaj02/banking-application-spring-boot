package com.example.banking_application_spring_boot.dto;

public record CreateAccountRequest(String accountHolderName, double initialDeposit) {

}
