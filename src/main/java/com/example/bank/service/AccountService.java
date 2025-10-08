package com.example.bank.service;

import com.example.bank.model.Account;
import com.example.bank.exception.ValidationException;
import java.sql.SQLException;
import java.util.List;

public interface AccountService {
    boolean createAccount(Account account) throws SQLException, ValidationException;
    Account getAccountByNumber(String accountNumber) throws SQLException;
    List<Account> getAllAccounts() throws SQLException;
    boolean updateAccount(Account account) throws SQLException, ValidationException;
    boolean deleteAccount(String accountNumber) throws SQLException;
}

