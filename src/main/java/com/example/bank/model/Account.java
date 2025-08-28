package com.example.bank.model;

import com.example.bank.storage.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class Account {
    public static void createTable() {
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
}
