package com.example.banking_application_spring_boot.listeners;


import com.example.banking_application_spring_boot.events.DepositEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// this is the conditional listener

@Component
public class HighAmountDepositListener {

    @EventListener(condition = "#event.amount > 50000")
    public void handleHighAmountDeposit(DepositEvent event) {
        // Logic to handle high amount deposit, e.g., send notification, log the event, etc.
        System.out.println("High amount deposit detected for account " + event.getAccountNumber() + ": $" + event.getAmount());
    }

    // TODO: Implement logic to send email notification to the user about the high amount deposit
    // TODO: Implement high amount transfer listener to handle transfers above a certain threshold
    // TODO: Implement logic to flag the account for review if multiple high amount deposits are detected within a short period of time
}
