package com.example.bank.service;

import com.example.bank.model.Customer;
import com.example.bank.exception.ValidationException;
import java.sql.SQLException;
import java.util.List;

public interface CustomerService {
    boolean createCustomer(Customer customer) throws SQLException, ValidationException;
    Customer getCustomerById(int customerId) throws SQLException;
    List<Customer> getAllCustomers() throws SQLException;
    boolean updateCustomer(Customer customer) throws SQLException, ValidationException;
    boolean deleteCustomer(int customerId) throws SQLException;
}