package com.example.banking_application_spring_boot.dto;

public record TransferRequestDto (Long receiverAccountNumber, double amount) {

}
