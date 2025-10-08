package com.example.bank.dao;

import com.example.bank.model.Account;
import com.example.bank.storage.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement; 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AccountDAO {

    private static final Logger logger = Logger.getLogger(AccountDAO.class.getName());

    public void createAccountTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_id INT AUTO_INCREMENT PRIMARY KEY,
                customer_id INT,
                account_type VARCHAR(50),
                bank_name VARCHAR(100),
                branch VARCHAR(100),
                balance DECIMAL(15,2),
                status VARCHAR(20),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                account_number VARCHAR(20) UNIQUE,
                ifsc_code VARCHAR(20),
                name_on_account VARCHAR(100),
                phone_linked VARCHAR(15),
                saving_amount DECIMAL(15,2),
                FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
            );
        """;

        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Accounts table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean insertAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_type, bank_name, branch, balance, status, account_number, ifsc_code, name_on_account, phone_linked, saving_amount) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        logger.info("DAO: Attempting to insert account: " + account.getAccountNumber());
        logger.info("DAO: SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerId());
            ps.setString(2, account.getAccountType());
            ps.setString(3, account.getBankName());
            ps.setString(4, account.getBranch());
            ps.setBigDecimal(5, account.getBalance());
            ps.setString(6, account.getStatus());
            ps.setString(7, account.getAccountNumber());
            ps.setString(8, account.getIfscCode());
            ps.setString(9, account.getNameOnAccount());
            ps.setString(10, account.getPhoneLinked());
            ps.setBigDecimal(11, account.getSavingAmount());

            int rowsAffected = ps.executeUpdate();
            logger.info("DAO: Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("✗ DAO: SQL Error: " + e.getMessage());
            throw e;
        }
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, accountNumber);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAccountFromResultSet(rs);
                }
            }
        }
        return null; 
    }

    public List<Account> getAllAccounts() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        }
        
        logger.info("DAO: Retrieved " + accounts.size() + " accounts");
        return accounts;
    }

    public boolean updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET customer_id = ?, account_type = ?, bank_name = ?, branch = ?, " +
                    "balance = ?, status = ?, ifsc_code = ?, name_on_account = ?, " +
                    "phone_linked = ?, saving_amount = ? WHERE account_number = ?";

        logger.info("DAO: Updating account: " + account.getAccountNumber());
        logger.info("DAO: SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerId());
            ps.setString(2, account.getAccountType());
            ps.setString(3, account.getBankName());
            ps.setString(4, account.getBranch());
            ps.setBigDecimal(5, account.getBalance());
            ps.setString(6, account.getStatus());
            ps.setString(7, account.getIfscCode());
            ps.setString(8, account.getNameOnAccount());
            ps.setString(9, account.getPhoneLinked());
            ps.setBigDecimal(10, account.getSavingAmount());
            
            ps.setString(11, account.getAccountNumber());

            int rowsAffected = ps.executeUpdate();
            logger.info("DAO: Rows affected by update: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.severe("✗ DAO: SQL Error updating account: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        logger.info("DAO: Deleting account: " + accountNumber);
        logger.info("DAO: SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            int rowsAffected = ps.executeUpdate();
            logger.info("DAO: Rows affected by delete: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.severe("DAO: SQL Error deleting account: " + e.getMessage());
            throw e;
        }
    }

    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBankName(rs.getString("bank_name"));
        account.setBranch(rs.getString("branch"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setIfscCode(rs.getString("ifsc_code"));
        account.setNameOnAccount(rs.getString("name_on_account"));
        account.setPhoneLinked(rs.getString("phone_linked"));
        account.setSavingAmount(rs.getBigDecimal("saving_amount"));
        return account;
    }


    public boolean updateAccountBalance(int accountId, BigDecimal amount, String operation) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, accountId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public BigDecimal getAccountBalance(int accountId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }
        return BigDecimal.ZERO;
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