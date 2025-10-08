package com.example.bank.dao;

import com.example.bank.model.Customer;
import com.example.bank.storage.DBConnection;

import java.sql.*;
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CustomerDAO {

    private static final Logger logger = Logger.getLogger(CustomerDAO.class.getName());

     public void createCustomerTable() {
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

    public boolean insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_name, username, password, aadhar_number, " +
                     "permanent_address, state, country, city, email, phone_number, status, " +
                     "dob, age, gender, father_name, mother_name) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getUsername());
            ps.setString(3, customer.getPassword());
            ps.setString(4, customer.getAadharNumber());
            ps.setString(5, customer.getPermanentAddress());
            ps.setString(6, customer.getState());
            ps.setString(7, customer.getCountry());
            ps.setString(8, customer.getCity());
            ps.setString(9, customer.getEmail());
            ps.setString(10, customer.getPhoneNumber());
            ps.setString(11, customer.getStatus());
            ps.setDate(12, new java.sql.Date(customer.getDob().getTime()));
            ps.setInt(13, customer.getAge());
            ps.setString(14, customer.getGender());
            ps.setString(15, customer.getFatherName());
            ps.setString(16, customer.getMotherName());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        }
        return customers;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET customer_name = ?, username = ?, password = ?, " +
                     "aadhar_number = ?, permanent_address = ?, state = ?, country = ?, " +
                     "city = ?, email = ?, phone_number = ?, status = ?, dob = ?, age = ?, " +
                     "gender = ?, father_name = ?, mother_name = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getUsername());
            ps.setString(3, customer.getPassword());
            ps.setString(4, customer.getAadharNumber());
            ps.setString(5, customer.getPermanentAddress());
            ps.setString(6, customer.getState());
            ps.setString(7, customer.getCountry());
            ps.setString(8, customer.getCity());
            ps.setString(9, customer.getEmail());
            ps.setString(10, customer.getPhoneNumber());
            ps.setString(11, customer.getStatus());
            ps.setDate(12, new java.sql.Date(customer.getDob().getTime()));
            ps.setInt(13, customer.getAge());
            ps.setString(14, customer.getGender());
            ps.setString(15, customer.getFatherName());
            ps.setString(16, customer.getMotherName());
            ps.setInt(17, customer.getCustomerId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setCustomerName(rs.getString("customer_name"));
        customer.setUsername(rs.getString("username"));
        customer.setPassword(rs.getString("password"));
        customer.setAadharNumber(rs.getString("aadhar_number"));
        customer.setPermanentAddress(rs.getString("permanent_address"));
        customer.setState(rs.getString("state"));
        customer.setCountry(rs.getString("country"));
        customer.setCity(rs.getString("city"));
        customer.setEmail(rs.getString("email"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setStatus(rs.getString("status"));
        customer.setDob(rs.getDate("dob")); 
        customer.setAge(rs.getInt("age"));
        java.sql.Timestamp createdOnTimestamp = rs.getTimestamp("created_on");
        if (createdOnTimestamp != null) {
            customer.setCreatedOn(createdOnTimestamp.toLocalDateTime());
        }
        
        java.sql.Timestamp modifiedOnTimestamp = rs.getTimestamp("modified_on");
        if (modifiedOnTimestamp != null) {
            customer.setModifiedOn(modifiedOnTimestamp.toLocalDateTime());
        }
        
        customer.setGender(rs.getString("gender"));
        customer.setFatherName(rs.getString("father_name"));
        customer.setMotherName(rs.getString("mother_name"));
        return customer;
    }
}