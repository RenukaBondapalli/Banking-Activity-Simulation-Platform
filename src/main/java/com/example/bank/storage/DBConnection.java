package com.example.bank.storage;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root"; 
    private static final String PASSWORD = "1747114@Renuka"; 

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}
