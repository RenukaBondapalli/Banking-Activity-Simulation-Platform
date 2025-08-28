package com.example.bank.app;

import com.example.bank.storage.DBConnection;
import java.sql.*;

public class JdbcTest {
    public static void main(String[] args) {
        System.out.println("Starting Bank Simulator...");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM accounts")) {

            System.out.println("Accounts in DB:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " +
                                   rs.getString("name") + " - " +
                                   rs.getDouble("balance"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
