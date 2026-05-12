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

}


// TODO: Add more loggers for withdraw and transfer methods