package com.example.banking_application_spring_boot.events;

public class WithdrawEvent {
    private String accountNumber;
    private double amount;

    public WithdrawEvent(String accountNumber, double amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }
}
