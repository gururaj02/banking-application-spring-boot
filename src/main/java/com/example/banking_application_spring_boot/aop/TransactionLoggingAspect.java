package com.example.banking_application_spring_boot.aop;

import com.example.banking_application_spring_boot.dto.AccountDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TransactionLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLoggingAspect.class);

    @AfterReturning(pointcut = "execution(* com.example.banking_application_spring_boot.service.impl.AccountServiceImpl.deposit(..))", returning = "result")
    public void logDepositSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        double amount = (double) args[0];

        AccountDto accountDto = (AccountDto) result;

        LOGGER.info("Deposit successful: Account Number = {}, Amount = {}", accountDto.id(), amount);
    }

    @AfterThrowing(pointcut = "execution(* com.example.banking_application_spring_boot.service.impl.AccountServiceImpl.deposit(..)) && args(amount, securityPin)", throwing = "exception")
    public void logDepositFailure(double amount, String securityPin, Exception exception) {
        LOGGER.error("Deposit failed: Amount = {}, Error = {}", amount, exception.getMessage());
    }

    @AfterReturning(pointcut = "execution(* com.example.banking_application_spring_boot.service.impl.AccountServiceImpl.withdraw(..))", returning = "result")
    public void logWithdrawSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        double amount = (double) args[0];

        AccountDto accountDto = (AccountDto) result;

        LOGGER.info("Withdraw successful: Account Number = {}, Amount = {}", accountDto.id(), amount);
    }

    @AfterThrowing(pointcut = "execution(* com.example.banking_application_spring_boot.service.impl.AccountServiceImpl.withdraw(..)) && args(amount, securityPin)", throwing = "exception")
    public void logWithdrawFailure(double amount, String securityPin, Exception exception) {
        LOGGER.error("Withdraw failed: Amount = {}, Error = {}", amount, exception.getMessage());
    }

    @AfterReturning(pointcut = "execution(* com.example.banking_application_spring_boot.service.impl.AccountServiceImpl.transfer(..))", returning = "result")
    public void logTransferSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String receiverAccountNumber = (String) args[0];
        double amount = (double) args[1];
        AccountDto accountDto = (AccountDto) result;
        LOGGER.info("Transfer successful: From Account = {}, To Account = {}, Amount = {}", accountDto.id(), receiverAccountNumber, amount);
    }

    @AfterThrowing(pointcut = "execution(* com.example.banking_application_spring_boot.service.impl.AccountServiceImpl.transfer(..)) && args(receiverAccountNumber, amount, securityPin)", throwing = "exception")
    public void logTransferFailure(String receiverAccountNumber, double amount, String securityPin, Exception exception) {
        LOGGER.error("Transfer failed: To Account = {}, Amount = {}, Error = {}", receiverAccountNumber, amount, exception.getMessage());
    }
}
