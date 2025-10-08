package com.example.bank.service.impl;

import com.example.bank.dao.AccountDAO;
import com.example.bank.model.Account;
import com.example.bank.service.AccountService;
import com.example.bank.util.ValidationUtil;
import com.example.bank.exception.ValidationException;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());
    private AccountDAO dao = new AccountDAO();

    private void validateAccount(Account account) throws ValidationException {

        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            throw new ValidationException("Account number is required");
        }
        
        if (!ValidationUtil.isValidAccountNumber(account.getAccountNumber())) {
            throw new ValidationException("Invalid account number format");
        }

        if (!ValidationUtil.isValidPhone(account.getPhoneLinked())) {
            throw new ValidationException("Phone number must be 10 digits and not start with 0");
        }

        if (!ValidationUtil.isValidName(account.getNameOnAccount())) {
            throw new ValidationException("Name can only contain letters, spaces, and .'- characters");
        }

        if (!ValidationUtil.isValidIFSC(account.getIfscCode())) {
            throw new ValidationException("Invalid IFSC code format");
        }

        if (!ValidationUtil.isValidBalance(account.getBalance())) {
            throw new ValidationException("Balance cannot be negative");
        }

        if (account.getSavingAmount() != null && 
            !ValidationUtil.isValidBalance(account.getSavingAmount())) {
            throw new ValidationException("Saving amount cannot be negative");
        }

        if (account.getBalance().compareTo(new BigDecimal("1000")) < 0) {
            throw new ValidationException("Minimum balance should be 1000");
        }  
    }

    @Override
    public boolean createAccount(Account account) throws SQLException, ValidationException {
        try {
            logger.info("Service: Validating account: " + account.getAccountNumber());
            validateAccount(account);
            
            logger.info("Service: Creating account: " + account.getAccountNumber());
            boolean result = dao.insertAccount(account);
            
            if (result) {
                logger.info("Service: Account created successfully: " + account.getAccountNumber());
            } else {
                logger.warning("Service: Failed to create account: " + account.getAccountNumber());
            }
            return result;
            
        } catch (ValidationException e) {
            logger.warning("Service: Validation failed: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            logger.severe("Service: SQL Error creating account: " + e.getMessage());
            throw e;
        }
    }

    @Override
        public Account getAccountByNumber(String accountNumber) throws SQLException {
            try {
                logger.info("Service: Retrieving account: " + accountNumber);
                Account account = dao.getAccountByNumber(accountNumber);
                
                if (account != null) {
                    logger.info("Service: Account found: " + accountNumber);
                } else {
                    logger.warning("Service: Account not found: " + accountNumber);
                }
                return account;
                
            } catch (SQLException e) {
                logger.severe("Service: SQL Error retrieving account: " + e.getMessage());
                throw e;
            }
        }

    @Override
        public List<Account> getAllAccounts() throws SQLException {
            try {
                logger.info("Service: Retrieving all accounts");
                List<Account> accounts = dao.getAllAccounts();
                logger.info("Service: Retrieved " + accounts.size() + " accounts");
                return accounts;
            } catch (SQLException e) {
                logger.severe("Service: SQL Error retrieving all accounts: " + e.getMessage());
                throw e;
            }
        }

    @Override
    public boolean updateAccount(Account account) throws SQLException, ValidationException {
        try {
            logger.info("Service: Validating account for update: " + account.getAccountNumber());
            validateAccount(account);
            
            logger.info("Service: Updating account: " + account.getAccountNumber());
            boolean result = dao.updateAccount(account);
            
            if (result) {
                logger.info("Service: Account updated successfully: " + account.getAccountNumber());
            } else {
                logger.warning("Service: Failed to update account: " + account.getAccountNumber());
            }
            return result;
            
        } catch (ValidationException e) {
            logger.warning("Service: Validation failed during update: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            logger.severe("Service: SQL Error updating account: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteAccount(String accountNumber) throws SQLException {
        try {
            logger.info("Service: Deleting account: " + accountNumber);
            boolean result = dao.deleteAccount(accountNumber);
            
            if (result) {
                logger.info("Service: Account deleted successfully: " + accountNumber);
            } else {
                logger.warning("Service: Failed to delete account (not found): " + accountNumber);
            }
            return result;
            
        } catch (SQLException e) {
            logger.severe("Service: SQL Error deleting account: " + e.getMessage());
            throw e;
        }
    }
}