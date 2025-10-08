package com.example.bank.dao;

import com.example.bank.model.Transaction;
import com.example.bank.storage.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TransactionDAO {

    private static final Logger logger = Logger.getLogger(TransactionDAO.class.getName());

    public void createTransactionTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                utr_number VARCHAR(50) UNIQUE,
                transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                transaction_amount DECIMAL(15,2),
                debited_date TIMESTAMP NULL,
                account_id INT,
                balance_amount DECIMAL(15,2),
                description TEXT,
                modified_by VARCHAR(100),
                receiver VARCHAR(100),
                transaction_type VARCHAR(20),
                mode_of_transaction VARCHAR(20),
                FOREIGN KEY (account_id) REFERENCES accounts(account_id)
            );
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Transactions table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


 
    public boolean createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (utr_number, transaction_amount, account_id, " +
                     "balance_amount, description, transaction_type, mode_of_transaction, receiver) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transaction.getUtrNumber());
            ps.setBigDecimal(2, transaction.getTransactionAmount());
            ps.setInt(3, transaction.getAccountId());
            ps.setBigDecimal(4, transaction.getBalanceAmount());
            ps.setString(5, transaction.getDescription());
            ps.setString(6, transaction.getTransactionType());
            ps.setString(7, transaction.getModeOfTransaction());
            ps.setString(8, transaction.getReceiver());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT t.*, " +
                    "sa.account_number AS senderAccountNumber, " +
                    "ra.account_number AS receiverAccountNumber " +
                    "FROM transactions t " +
                    "JOIN accounts sa ON sa.account_id = t.account_id " +
                    "LEFT JOIN accounts ra ON ra.account_id = t.receiver " +
                    "WHERE sa.account_number = ? OR ra.account_number = ? " +
                    "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            ps.setString(2, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
        }
        return transactions;
    }


    
    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
        }
        return transactions;
    }

   
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUtrNumber(rs.getString("utr_number"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        transaction.setTransactionAmount(rs.getBigDecimal("transaction_amount"));
        transaction.setDebitedDate(rs.getTimestamp("debited_date"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setBalanceAmount(rs.getBigDecimal("balance_amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setReceiver(rs.getString("receiver"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setModeOfTransaction(rs.getString("mode_of_transaction"));
        transaction.setSenderAccountNumber(rs.getString("senderAccountNumber"));
        transaction.setReceiverAccountNumber(rs.getString("receiverAccountNumber"));

        return transaction;
    }


  
    public int getAccountIdByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT account_id FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("account_id");
                }
            }
        }
        return -1; 
    }
}
