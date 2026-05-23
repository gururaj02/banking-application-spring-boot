package com.example.banking_application_spring_boot.listeners;

import com.example.banking_application_spring_boot.events.DepositEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LogDepositListener {

    @EventListener
    @Async
    @Order(1) // Set the order of execution if there are multiple listeners for the same event
    public void logDeposit(DepositEvent event) throws InterruptedException {
        System.out.println("Deposit of $" + event.getAmount() + " has been made to account ID: " + event.getAccountNumber());

        Thread.sleep(1000); // Simulate time-consuming logging operation

        System.out.println("Deposit logged successfully for account ID: " + event.getAccountNumber());
    }
}
