package com.example.bank.service.impl;

import com.example.bank.dao.CustomerDAO;
import com.example.bank.model.Customer;
import com.example.bank.service.CustomerService;
import com.example.bank.util.ValidationUtil;
import com.example.bank.exception.ValidationException;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());
    private CustomerDAO dao = new CustomerDAO();

    private void validateCustomer(Customer customer) throws ValidationException {
        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            throw new ValidationException("Customer name is required");
        }

        if (customer.getUsername() == null || customer.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }

        if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }

        if (customer.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }

        if (!ValidationUtil.isValidPhone(customer.getPhoneNumber())) {
            throw new ValidationException("Phone number must be 10 digits and not start with 0");
        }

        if (customer.getEmail() != null && !customer.getEmail().isEmpty() && 
            !ValidationUtil.isValidEmail(customer.getEmail())) {
            throw new ValidationException("Invalid email format");
        }

        if (customer.getAadharNumber() != null && !customer.getAadharNumber().isEmpty() && 
            !customer.getAadharNumber().matches("^[0-9]{12}$")) {
            throw new ValidationException("Aadhar number must be 12 digits");
        }

        if (customer.getDob() == null) {
            throw new ValidationException("Date of birth is required");
        }

        if (customer.getAge() < 18) {
            throw new ValidationException("Customer must be at least 18 years old");
        }

        if (!ValidationUtil.isValidPin(customer.getPin())) {
            throw new ValidationException("PIN must be exactly 4 numeric digits.");
        }


    }

    @Override
    public boolean createCustomer(Customer customer) throws SQLException, ValidationException {
        try {
            validateCustomer(customer);
            return dao.insertCustomer(customer);
        } catch (ValidationException e) {
            logger.warning("Validation failed: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Customer getCustomerById(int customerId) throws SQLException {
        return dao.getCustomerById(customerId);
    }

    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        return dao.getAllCustomers();
    }

    @Override
    public boolean updateCustomer(Customer customer) throws SQLException, ValidationException {
        try {
            validateCustomer(customer);
            return dao.updateCustomer(customer);
        } catch (ValidationException e) {
            logger.warning("Validation failed during update: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteCustomer(int customerId) throws SQLException {
        return dao.deleteCustomer(customerId);
    }
}
