package com.example.bank.controller;

import com.example.bank.model.Account;
import com.example.bank.service.AccountService;
import com.example.bank.exception.ValidationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account validAccount;
    private Account invalidAccount;

    @BeforeEach
    void setUp() {
        validAccount = new Account();
        validAccount.setAccountNumber("ACC001");
        validAccount.setCustomerId(1);
        validAccount.setAccountType("SAVINGS");
        validAccount.setBalance(new BigDecimal("5000.00"));
        validAccount.setPhoneLinked("9876543210");
        validAccount.setNameOnAccount("John Doe");
        validAccount.setIfscCode("SBIN0001234");
        validAccount.setStatus("ACTIVE");


        invalidAccount = new Account();
        invalidAccount.setAccountNumber("ACC001");
        invalidAccount.setBalance(new BigDecimal("-100.00"));
        invalidAccount.setPhoneLinked("0123456789");
        invalidAccount.setNameOnAccount("John123");
        invalidAccount.setIfscCode("INVALID");
    }



    @Test
    void testCreateAccount_Success() throws SQLException, ValidationException {
        when(accountService.createAccount(any(Account.class))).thenReturn(true);

        Response response = accountController.createAccount(validAccount);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals("Account Created Successfully!", response.getEntity());
    }


    @Test
    void testCreateAccount_ValidationFailure_Phone() throws SQLException, ValidationException {
        when(accountService.createAccount(any(Account.class)))
            .thenThrow(new ValidationException("Phone number must be 10 digits and not start with 0"));

        Response response = accountController.createAccount(invalidAccount);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Phone number"));
    }

    @Test
    void testCreateAccount_ValidationFailure_Balance() throws SQLException, ValidationException {
        when(accountService.createAccount(any(Account.class)))
            .thenThrow(new ValidationException("Balance cannot be negative"));

        Response response = accountController.createAccount(invalidAccount);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Balance"));
    }

    @Test
    void testCreateAccount_ValidationFailure_Name() throws SQLException, ValidationException {
        when(accountService.createAccount(any(Account.class)))
            .thenThrow(new ValidationException("Name can only contain letters, spaces, and .'- characters"));

        Response response = accountController.createAccount(invalidAccount);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Name"));
    }

    @Test
    void testCreateAccount_DatabaseFailure() throws SQLException, ValidationException {
        when(accountService.createAccount(any(Account.class)))
            .thenThrow(new SQLException("Database connection failed"));

        Response response = accountController.createAccount(validAccount);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }



    @Test
    void testGetAccount_Success() throws SQLException {
        when(accountService.getAccountByNumber("ACC001")).thenReturn(validAccount);

        Response response = accountController.getAccount("ACC001");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(validAccount, response.getEntity());
    }


    @Test
    void testGetAccount_NotFound() throws SQLException {
        when(accountService.getAccountByNumber("INVALID")).thenReturn(null);

        Response response = accountController.getAccount("INVALID");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Account not found: INVALID", response.getEntity());
    }

    @Test
    void testGetAccount_DatabaseConnectionError() throws SQLException {
        when(accountService.getAccountByNumber("ACC001"))
            .thenThrow(new SQLException("Connection timeout"));

        Response response = accountController.getAccount("ACC001");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testGetAccount_DatabaseSyntaxError() throws SQLException {
        when(accountService.getAccountByNumber("ACC001"))
            .thenThrow(new SQLException("SQL syntax error"));

        Response response = accountController.getAccount("ACC001");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testGetAccount_EmptyAccountNumber() throws SQLException {
        when(accountService.getAccountByNumber("")).thenReturn(null);

        Response response = accountController.getAccount("");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Account not found: ", response.getEntity());
    }



    @Test
    void testUpdateAccount_Success() throws SQLException, ValidationException {
        when(accountService.updateAccount(any(Account.class))).thenReturn(true);

        Response response = accountController.updateAccount("ACC001", validAccount);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Account updated successfully!", response.getEntity());
    }


    @Test
    void testUpdateAccount_NumberMismatch() {
        validAccount.setAccountNumber("DIFFERENT");

        Response response = accountController.updateAccount("ACC001", validAccount);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Account number in path does not match request body", response.getEntity());
    }

    @Test
    void testUpdateAccount_ValidationFailure_IFSC() throws SQLException, ValidationException {
        when(accountService.updateAccount(any(Account.class)))
            .thenThrow(new ValidationException("Invalid IFSC code format"));

        Response response = accountController.updateAccount("ACC001", invalidAccount);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("IFSC"));
    }

    @Test
    void testUpdateAccount_NotFound() throws SQLException, ValidationException {
        when(accountService.updateAccount(any(Account.class))).thenReturn(false);

        Response response = accountController.updateAccount("ACC001", validAccount);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Account not found: ACC001", response.getEntity());
    }

    @Test
    void testUpdateAccount_DatabaseFailure() throws SQLException, ValidationException {
        when(accountService.updateAccount(any(Account.class)))
            .thenThrow(new SQLException("Update failed due to constraint violation"));

        Response response = accountController.updateAccount("ACC001", validAccount);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }



    @Test
    void testDeleteAccount_Success() throws SQLException {
        when(accountService.deleteAccount("ACC001")).thenReturn(true);

        Response response = accountController.deleteAccount("ACC001");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Account deleted successfully!", response.getEntity());
    }


    @Test
    void testDeleteAccount_NotFound() throws SQLException {
        when(accountService.deleteAccount("INVALID")).thenReturn(false);

        Response response = accountController.deleteAccount("INVALID");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Account not found: INVALID", response.getEntity());
    }

    @Test
    void testDeleteAccount_ForeignKeyConstraint() throws SQLException {
        when(accountService.deleteAccount("ACC001"))
            .thenThrow(new SQLException("Cannot delete due to foreign key constraint"));

        Response response = accountController.deleteAccount("ACC001");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testDeleteAccount_DatabaseConnectionError() throws SQLException {
        when(accountService.deleteAccount("ACC001"))
            .thenThrow(new SQLException("Database connection lost"));

        Response response = accountController.deleteAccount("ACC001");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testDeleteAccount_EmptyAccountNumber() throws SQLException {
        when(accountService.deleteAccount("")).thenReturn(false);

        Response response = accountController.deleteAccount("");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Account not found: ", response.getEntity());
    }
}