package com.example.bank.controller;

import com.example.bank.model.Transaction;
import com.example.bank.service.impl.TransactionServiceImpl;
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
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionServiceImpl transactionServiceImpl;

    private Transaction validTransaction;
    private Transaction invalidTransaction;

    @BeforeEach
    void setUp() {
        validTransaction = new Transaction();
        validTransaction.setTransactionAmount(new BigDecimal("1000"));
        validTransaction.setUtrNumber("UTRTEST123");
        validTransaction.setModeOfTransaction("UPI");
        validTransaction.setAccountNumber("ACC1000001"); 
        validTransaction.setReceiver("ACC1000002");

        invalidTransaction = new Transaction();
        invalidTransaction.setTransactionAmount(new BigDecimal("-100"));
        invalidTransaction.setUtrNumber("");
        invalidTransaction.setModeOfTransaction("UPI");
        invalidTransaction.setAccountNumber("INVALID_ACC");
        invalidTransaction.setReceiver("ACC1000002");
    }

    
    @Test
    void deposit_success() throws SQLException, ValidationException {
        when(transactionServiceImpl.deposit(any(Transaction.class))).thenReturn(true);

        Response response = transactionController.deposit(validTransaction);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Deposit successful"));
    }

    @Test
    void deposit_validationFailure() throws SQLException, ValidationException {
        when(transactionServiceImpl.deposit(any(Transaction.class)))
                .thenThrow(new ValidationException("Invalid transaction"));

        Response response = transactionController.deposit(invalidTransaction);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid transaction"));
    }

    @Test
    void deposit_databaseFailure() throws SQLException, ValidationException {
        when(transactionServiceImpl.deposit(any(Transaction.class)))
                .thenThrow(new SQLException("Database error"));

        Response response = transactionController.deposit(validTransaction);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    
    @Test
    void withdraw_success() throws SQLException, ValidationException {
        when(transactionServiceImpl.withdraw(any(Transaction.class))).thenReturn(true);

        Response response = transactionController.withdraw(validTransaction);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Withdrawal successful"));
    }

    @Test
    void withdraw_validationFailure() throws SQLException, ValidationException {
        when(transactionServiceImpl.withdraw(any(Transaction.class)))
                .thenThrow(new ValidationException("Insufficient balance"));

        Response response = transactionController.withdraw(validTransaction);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Insufficient balance"));
    }

    @Test
    void withdraw_databaseFailure() throws SQLException, ValidationException {
        when(transactionServiceImpl.withdraw(any(Transaction.class)))
                .thenThrow(new SQLException("Database error"));

        Response response = transactionController.withdraw(validTransaction);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void withdraw_invalidAccount() throws SQLException, ValidationException {
        when(transactionServiceImpl.withdraw(any(Transaction.class)))
                .thenThrow(new ValidationException("Invalid account number"));

        Response response = transactionController.withdraw(invalidTransaction);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid account number"));
    }

    
    @Test
    void transfer_success() throws SQLException, ValidationException {
        when(transactionServiceImpl.transfer(any(Transaction.class))).thenReturn(true);

        Response response = transactionController.transfer(validTransaction);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Transfer successful"));
    }

    @Test
    void transfer_validationFailure() throws SQLException, ValidationException {
        when(transactionServiceImpl.transfer(any(Transaction.class)))
                .thenThrow(new ValidationException("Insufficient balance"));

        Response response = transactionController.transfer(validTransaction);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Insufficient balance"));
    }

    @Test
    void transfer_databaseFailure() throws SQLException, ValidationException {
        when(transactionServiceImpl.transfer(any(Transaction.class)))
                .thenThrow(new SQLException("Database error"));

        Response response = transactionController.transfer(validTransaction);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void transfer_invalidAccount() throws SQLException, ValidationException {
        when(transactionServiceImpl.transfer(any(Transaction.class)))
                .thenThrow(new ValidationException("Invalid sender or receiver"));

        Response response = transactionController.transfer(invalidTransaction);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid sender or receiver"));
    }

    
    @Test
    void getTransactions_success() throws SQLException {
        List<Transaction> txnList = new ArrayList<>();
        txnList.add(validTransaction);

        when(transactionServiceImpl.getTransactionsByAccountNumber("ACC1000001")).thenReturn(txnList);

        Response response = transactionController.getTransactionsByAccountNumber("ACC1000001");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(txnList, response.getEntity());
    }

    @Test
    void getTransactions_notFound() throws SQLException {
        when(transactionServiceImpl.getTransactionsByAccountNumber("ACC1000001"))
                .thenReturn(new ArrayList<>());

        Response response = transactionController.getTransactionsByAccountNumber("ACC1000001");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("No transactions found"));
    }

    @Test
    void getTransactions_databaseFailure() throws SQLException {
        when(transactionServiceImpl.getTransactionsByAccountNumber("ACC1000001"))
                .thenThrow(new SQLException("Database error"));

        Response response = transactionController.getTransactionsByAccountNumber("ACC1000001");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }
}
