package com.example.banking_application_spring_boot.listeners;

import com.example.banking_application_spring_boot.events.DepositEvent;
import com.example.banking_application_spring_boot.events.WithdrawEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    @EventListener
    @Async
    @Order(2) // Ensure this runs after the transaction is completed
    public void handleDepositAndSendEmail(DepositEvent event) throws InterruptedException {
        // Simulate sending an email notification for a deposit
        System.out.println("Sending email to " + event.getAccountNumber() + ": Your account has been credited with $" + event.getAmount());
        Thread.sleep(3000); // Simulate delay in sending email
        System.out.println("Email sent to " + event.getAccountNumber() + ": Your account has been credited with $" + event.getAmount());
    }

    @EventListener
    @Async
    public void handleWithdrawalAndSendEmail(WithdrawEvent event) throws InterruptedException {
        // Simulate sending an email notification for a withdrawal
        System.out.println("Sending email to " + event.getAccountNumber() + ": Your account has been debited with $" + event.getAmount());
        Thread.sleep(3000); // Simulate delay in sending email
        System.out.println("Email sent to " + event.getAccountNumber() + ": Your account has been debited with $" + event.getAmount());
    }
}
