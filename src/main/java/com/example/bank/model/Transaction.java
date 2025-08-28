package com.example.bank.model;

import com.example.bank.storage.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class Transaction {
    public static void createTable() {
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
                transaction_type VARCHAR(20), -- deposit, withdrawal
                mode_of_transaction VARCHAR(20), -- credit, debit, upi, qr
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
}
