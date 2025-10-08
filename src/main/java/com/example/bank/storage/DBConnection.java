package com.example.bank.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root"; 
    private static final String PASSWORD = "1747114@Renuka"; 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        connection.setAutoCommit(true);
        System.out.println("Database connection established!");
        return connection;
    }
}