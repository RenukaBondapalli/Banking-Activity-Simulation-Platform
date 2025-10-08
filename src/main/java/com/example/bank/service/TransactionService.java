package com.example.bank.service;

import com.example.bank.model.Transaction;
import com.example.bank.exception.ValidationException;
import java.sql.SQLException;
import java.util.List;

public interface TransactionService {
    boolean deposit(Transaction transaction) throws SQLException, ValidationException;
    boolean withdraw(Transaction transaction) throws SQLException, ValidationException;
    boolean transfer(Transaction transaction) throws SQLException, ValidationException;
    int getAccountIdByNumber(String accountNumber) throws SQLException;
    List<Transaction> getTransactionsByAccountNumber(String accountNumber) throws SQLException;
    List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException;
}