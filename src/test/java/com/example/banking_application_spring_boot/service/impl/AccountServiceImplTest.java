package com.example.banking_application_spring_boot.service.impl;

import com.example.banking_application_spring_boot.dto.AccountDto;
import com.example.banking_application_spring_boot.dto.CreateAccountRequest;
import com.example.banking_application_spring_boot.entity.Account;
import com.example.banking_application_spring_boot.entity.Users;
import com.example.banking_application_spring_boot.exception.AccountException;
import com.example.banking_application_spring_boot.exception.DepositOrWithdrawZeroRsException;
import com.example.banking_application_spring_boot.exception.InsufficientBalanceException;
import com.example.banking_application_spring_boot.repository.AccountRepository;
import com.example.banking_application_spring_boot.repository.TransactionRepository;
import com.example.banking_application_spring_boot.repository.UserDetailsRepository;
import com.example.banking_application_spring_boot.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateAccount_success() {

        // Arrange
        mockSecurityContext("gururaj");

        CreateAccountRequest request = new CreateAccountRequest(
                "Gururaj", 1000.0, "1234"
        );

        Users user = new Users();
        user.setAccount(null);

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("1234"))
                .thenReturn("encoded pin");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountDto result = accountService.createAccount(request);

        // Assert
        assertNotNull(result);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_accountAlreadyExists() {

        // Arrange
        mockSecurityContext("gururaj");

        CreateAccountRequest request = new CreateAccountRequest(
                "Gururaj", 1000.0, "1234"
        );

        Account account = new Account();
        account.setActive(true);

        Users user = new Users();
        user.setAccount(account);

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(AccountException.class, () -> {
            accountService.createAccount(request);
        });
    }

    @Test
    void testCreateAccount_reactivateAccount() {

        // Arrange
        mockSecurityContext("gururaj");

        CreateAccountRequest request = new CreateAccountRequest(
                "Gururaj", 1000.0, "1234"
        );

        Account account = new Account();
        account.setActive(false);

        Users user = new Users();
        user.setAccount(account);

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("1234"))
                .thenReturn("encodedPin");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountDto result = accountService.createAccount(request);

        // Assert
        assertNotNull(result);
        assertTrue(account.isActive());
        verify(accountRepository).save(account);
    }

    @Test
    void testCreateAccount_userNotFound() {

        // Arrange
        mockSecurityContext("gururaj");

        CreateAccountRequest request = new CreateAccountRequest(
                "Gururaj", 1000.0, "1234"
        );

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.createAccount(request);
        });
    }

    @Test
    void testCreateAccount_invalidDeposit() {

        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(
                "Gururaj", 0.0, "1234"
        );

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () -> {
            accountService.createAccount(request);
        });
    }

    @Test
    void testDeposit_invalidDeposit() {
        // Act & Assert
        assertThrows(DepositOrWithdrawZeroRsException.class, () -> {
            accountService.deposit(0, "1234");
        });

        // Verify no repository interaction
        verifyNoInteractions(userDetailsRepository);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void testDeposit_depositSuccess() {
        // Arrange
        mockSecurityContext("gururaj");

        Users user = new Users();

        Account account = new Account();
        account.setActive(true);
        account.setBalance(1000);
        account.setSecurityPin("encodedPin");

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("1234", "encodedPin"))
                .thenReturn(true);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // Act
        AccountDto result = accountService.deposit(500, "1234");

        // Assert
        assertNotNull(result);

        // Verify balance updated
        assertEquals(1500, account.getBalance());

        // Verify save called
        verify(accountRepository).save(account);

        // Verify transaction recorded
        verify(transactionService).recordDeposit(account, 500);
    }

    @Test
    void testWithDraw_invalidWithdrawAmount() {
        // Act & Assert
        assertThrows(DepositOrWithdrawZeroRsException.class, () -> {
            accountService.withdraw(0, "1234");
        });

        // Verify no repository interaction
        verifyNoInteractions(userDetailsRepository);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void testWithDraw_withdrawSuccess() {
        // Arrange
        mockSecurityContext("gururaj");

        Users user = new Users();

        Account account = new Account();
        account.setActive(true);
        account.setBalance(1000);
        account.setSecurityPin("encodedPin");

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("1234", "encodedPin"))
                .thenReturn(true);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // Act
        AccountDto result = accountService.withdraw(500, "1234");

        // Assert
        assertNotNull(result);

        // Verify balance updated
        assertEquals(500, account.getBalance());

        // Verify save called
        verify(accountRepository).save(account);

        // Verify transaction recorded
        verify(transactionService).recordWithdraw(account, 500);
    }

    @Test
    void deleteAccount_Success() {
        // Arrange
        mockSecurityContext("gururaj");

        Users user = new Users();

        Account account = new Account();
        account.setActive(true);
        account.setBalance(0);
        account.setSecurityPin("encodedPin");

        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("1234", "encodedPin"))
                .thenReturn(true);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        accountService.deleteAccount("1234");

        // Assert
        assertFalse(account.isActive());

        verify(accountRepository).save(account);
    }

    @Test
    void deleteAccount_ShouldThrowException_WhenAccountAlreadyClosed() {
        // Arrange
        mockSecurityContext("gururaj");

        Users user = new Users();

        Account account = new Account();
        account.setActive(false);
        account.setBalance(0);
        account.setSecurityPin("encodedPin");

        // Mock repository calls
        when(userDetailsRepository.findByUsername("gururaj"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(Optional.of(account));

        // Act & Assert
        AccountException exception = assertThrows(AccountException.class, () -> accountService.deleteAccount("1234"));

        assertEquals("Account is already closed", exception.getMessage());

        verify(accountRepository, never()).save(account);
    }
}

// TODO: Add Simple events - like sending email etc., (for learning)
// TODO: learn monitoring(Actuators)
// TODO: Try to add scheduling anywhere possible (just for learning concepts)
// TODO: Try to add redis in getAllAccounts or anywhere possible