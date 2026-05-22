package com.example.banking_application_spring_boot.listeners;

import com.example.banking_application_spring_boot.events.DepositEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    @EventListener
    public void handleDepositAndSendEmail(DepositEvent event) {
        // Simulate sending an email notification for a deposit
        System.out.println("Email sent to " + event.getAccountNumber() + ": Your account has been credited with $" + event.getAmount());
    }
}
