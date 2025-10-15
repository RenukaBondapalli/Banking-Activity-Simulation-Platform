package com.example.bank.service.impl;

import com.example.bank.dao.AccountDAO;
import com.example.bank.dao.TransactionDAO;
import com.example.bank.model.Transaction;
import com.example.bank.model.Account;
import com.example.bank.dao.CustomerDAO;
import com.example.bank.model.Customer;
import com.example.bank.service.TransactionService;
import com.example.bank.service.NotificationService;
import com.example.bank.exception.ValidationException;
import com.example.bank.storage.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = Logger.getLogger(TransactionServiceImpl.class.getName());
    private TransactionDAO transactionDAO = new TransactionDAO();
    private AccountDAO accountDAO = new AccountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private NotificationService notificationService = new NotificationService();

    @Override
    public boolean deposit(Transaction transaction) throws SQLException, ValidationException {
        try {
            logger.info("Processing deposit for account ID: " + transaction.getAccountId());

            validateTransaction(transaction);

            boolean balanceUpdated = accountDAO.updateAccountBalance(
                    transaction.getAccountId(),
                    transaction.getTransactionAmount(),
                    "CREDIT"
            );

            if (!balanceUpdated) {
                throw new SQLException("Failed to update account balance");
            }

            BigDecimal currentBalance = accountDAO.getAccountBalance(transaction.getAccountId());
            transaction.setBalanceAmount(currentBalance);
            transaction.setTransactionType("DEPOSIT");

            boolean success = transactionDAO.createTransaction(transaction);


            Account acc = accountDAO.getAccountByNumber(transaction.getAccountNumber());
            if (acc != null) {
                Customer cust = customerDAO.getCustomerById(acc.getCustomerId());
                if (cust != null) {
                    notificationService.sendDepositNotification(
                            cust.getCustomerName(),
                            acc.getAccountNumber(),
                            cust.getEmail(),
                            transaction.getTransactionAmount().toString()
                    );
                }
            }

            return success;

        } catch (SQLException e) {
            logger.severe("Error processing deposit: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean withdraw(Transaction transaction) throws SQLException, ValidationException {


        try {
            
            String storedPin = customerDAO.getPinByAccountNumber(transaction.getAccountNumber());
            if (storedPin == null || !storedPin.equals(transaction.getPin())) {
                throw new ValidationException("Invalid PIN. Transaction failed.");
            }

            int accountId = accountDAO.getAccountIdByNumber(transaction.getAccountNumber());
            if (accountId <= 0) {
                throw new ValidationException("Invalid account number");
            }
            transaction.setAccountId(accountId);

            logger.info("Processing withdrawal for account ID: " + accountId);

            if (transaction.getUtrNumber() == null || transaction.getUtrNumber().trim().isEmpty()) {
                transaction.setUtrNumber("UTR" + System.currentTimeMillis());
            }

            validateTransaction(transaction);

            BigDecimal currentBalance = accountDAO.getAccountBalance(accountId);
            if (currentBalance.compareTo(transaction.getTransactionAmount()) < 0) {
                throw new ValidationException("Insufficient balance");
            }

            boolean balanceUpdated = accountDAO.updateAccountBalance(
                    accountId,
                    transaction.getTransactionAmount().negate(),
                    "DEBIT"
            );

            if (!balanceUpdated) {
                throw new SQLException("Failed to update account balance");
            }

            BigDecimal updatedBalance = accountDAO.getAccountBalance(accountId);
            transaction.setBalanceAmount(updatedBalance);
            transaction.setTransactionType("WITHDRAWAL");

            boolean success = transactionDAO.createTransaction(transaction);

            
            Account acc = accountDAO.getAccountByNumber(transaction.getAccountNumber());
            if (acc != null) {
                Customer cust = customerDAO.getCustomerById(acc.getCustomerId());
                if (cust != null) {
                    notificationService.sendWithdrawalNotification(
                            cust.getCustomerName(),
                            acc.getAccountNumber(),
                            cust.getEmail(),
                            transaction.getTransactionAmount().toString()
                    );
                }
            }

            return success;

        } catch (SQLException e) {
            logger.severe("Error processing withdrawal: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean transfer(Transaction transaction) throws SQLException, ValidationException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            validateTransaction(transaction);

            String storedPin = customerDAO.getPinByAccountNumber(transaction.getSenderAccountNumber());
            if (storedPin == null || !storedPin.equals(transaction.getPin())) {
                throw new ValidationException("Invalid PIN. Transaction failed.");
            }

            int senderAccountId = accountDAO.getAccountIdByNumber(transaction.getSenderAccountNumber());
            int receiverAccountId = accountDAO.getAccountIdByNumber(transaction.getReceiverAccountNumber());

            if (senderAccountId <= 0 || receiverAccountId <= 0) {
                throw new ValidationException("Invalid sender or receiver account number");
            }

            if (senderAccountId == receiverAccountId) {
                throw new ValidationException("Sender and receiver account cannot be the same");
            }

            BigDecimal senderBalance = accountDAO.getAccountBalance(senderAccountId);
            if (senderBalance.compareTo(transaction.getTransactionAmount()) < 0) {
                throw new ValidationException("Insufficient balance in sender account");
            }

            boolean senderUpdated = accountDAO.updateAccountBalance(senderAccountId,
                    transaction.getTransactionAmount().negate(), "DEBIT");
            if (!senderUpdated) {
                throw new SQLException("Failed to debit sender account");
            }

            boolean receiverUpdated = accountDAO.updateAccountBalance(receiverAccountId,
                    transaction.getTransactionAmount(), "CREDIT");
            if (!receiverUpdated) {
                throw new SQLException("Failed to credit receiver account");
            }

            
            Transaction senderTxn = new Transaction(
                    "UTR" + System.currentTimeMillis(),
                    transaction.getTransactionAmount(),
                    senderAccountId,
                    accountDAO.getAccountBalance(senderAccountId),
                    "Transfer to account " + transaction.getReceiverAccountNumber(),
                    "TRANSFER-DEBIT",
                    transaction.getModeOfTransaction(),
                    transaction.getReceiverAccountNumber()
            );
            senderTxn.setSenderAccountNumber(transaction.getSenderAccountNumber());
            transactionDAO.createTransaction(senderTxn);

        
            Transaction receiverTxn = new Transaction(
                    "UTR" + System.currentTimeMillis() + "R",
                    transaction.getTransactionAmount(),
                    receiverAccountId,
                    accountDAO.getAccountBalance(receiverAccountId),
                    "Transfer from account " + transaction.getSenderAccountNumber(),
                    "TRANSFER-CREDIT",
                    transaction.getModeOfTransaction(),
                    transaction.getSenderAccountNumber()
            );
            receiverTxn.setSenderAccountNumber(transaction.getSenderAccountNumber());
            transactionDAO.createTransaction(receiverTxn);

            
            Account senderAcc = accountDAO.getAccountByNumber(transaction.getSenderAccountNumber());
            Account receiverAcc = accountDAO.getAccountByNumber(transaction.getReceiverAccountNumber());
            if (senderAcc != null && receiverAcc != null) {
                Customer senderCust = customerDAO.getCustomerById(senderAcc.getCustomerId());
                Customer receiverCust = customerDAO.getCustomerById(receiverAcc.getCustomerId());
                if (senderCust != null && receiverCust != null) {
                    notificationService.sendTransferNotification(
                            senderCust.getCustomerName(),
                            senderAcc.getAccountNumber(),
                            senderCust.getEmail(),
                            receiverCust.getCustomerName(),
                            receiverAcc.getAccountNumber(),
                            receiverCust.getEmail(),
                            transaction.getTransactionAmount().toString()
                    );
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        }
    }

    @Override
    public int getAccountIdByNumber(String accountNumber) throws SQLException {
        return accountDAO.getAccountIdByNumber(accountNumber);
    }

    @Override
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) throws SQLException {
        return transactionDAO.getTransactionsByAccountNumber(accountNumber);
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }

    private void validateTransaction(Transaction transaction) throws ValidationException {
        if (transaction.getTransactionAmount() == null ||
                transaction.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transaction amount must be positive");
        }

        if (transaction.getModeOfTransaction() == null || transaction.getModeOfTransaction().trim().isEmpty()) {
            throw new ValidationException("Mode of transaction cannot be empty");
        }
    }
}
