package com.example.bank.model;

import com.example.bank.storage.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class Customer {
    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS customers (
                customer_id INT AUTO_INCREMENT PRIMARY KEY,
                customer_name VARCHAR(100) NOT NULL,
                username VARCHAR(100) UNIQUE,
                password VARCHAR(255) NOT NULL,
                aadhar_number VARCHAR(20),
                permanent_address VARCHAR(255),
                state VARCHAR(50),
                country VARCHAR(50),
                city VARCHAR(50),
                email VARCHAR(100),
                phone_number VARCHAR(15),
                status VARCHAR(20),
                dob DATE,
                age INT,
                created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                gender VARCHAR(10),
                father_name VARCHAR(100),
                mother_name VARCHAR(100)
            );
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Customers table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
