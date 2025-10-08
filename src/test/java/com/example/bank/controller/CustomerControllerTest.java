package com.example.bank.controller;

import com.example.bank.model.Customer;
import com.example.bank.service.CustomerService;
import com.example.bank.exception.ValidationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer validCustomer;
    private Customer invalidCustomer;

    @BeforeEach
    void setUp() {
        validCustomer = new Customer();
        validCustomer.setCustomerId(1);
        validCustomer.setCustomerName("John Doe"); 
        validCustomer.setUsername("johndoe");
        validCustomer.setPassword("secure123"); 
        validCustomer.setPhoneNumber("9876543210"); 
        validCustomer.setEmail("john.doe@email.com"); 
        validCustomer.setDob(Date.valueOf(LocalDate.of(1990, 5, 15)));
        validCustomer.setAge(33); 
        validCustomer.setAadharNumber("123456789012"); 
        validCustomer.setStatus("ACTIVE");

    
        invalidCustomer = new Customer();
        invalidCustomer.setCustomerId(1);
        invalidCustomer.setCustomerName("John123"); 
        invalidCustomer.setUsername("");
        invalidCustomer.setPassword("123"); 
        invalidCustomer.setPhoneNumber("0123456789"); 
        invalidCustomer.setEmail("invalid-email"); 
        invalidCustomer.setAge(16); 
        invalidCustomer.setAadharNumber("123"); 
}


    @Test
    void testCreateCustomer_Success() throws SQLException, ValidationException {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(true);

        Response response = customerController.createCustomer(validCustomer);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals("Customer created successfully!", response.getEntity());
    }



    @Test
    void testCreateCustomer_ValidationFailure_Phone() throws SQLException, ValidationException {
        when(customerService.createCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Phone number must be 10 digits and not start with 0"));

        Response response = customerController.createCustomer(invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Phone number must be 10 digits and not start with 0"));
    }

    @Test
    void testCreateCustomer_ValidationFailure_Email() throws SQLException, ValidationException {
        when(customerService.createCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Invalid email format"));

        Response response = customerController.createCustomer(invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid email format"));
    }

    @Test
    void testCreateCustomer_ValidationFailure_Age() throws SQLException, ValidationException {
        when(customerService.createCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Customer must be at least 18 years old"));

        Response response = customerController.createCustomer(invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer must be at least 18 years old"));
    }

    @Test
    void testCreateCustomer_ValidationFailure_Password() throws SQLException, ValidationException {
        when(customerService.createCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Password must be at least 6 characters"));

        Response response = customerController.createCustomer(invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Password must be at least 6 characters"));
    }



    @Test
    void testGetCustomer_Success() throws SQLException {
        when(customerService.getCustomerById(1)).thenReturn(validCustomer);

        Response response = customerController.getCustomer(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(validCustomer, response.getEntity());
    }


    @Test
    void testGetCustomer_NotFound() throws SQLException {
        when(customerService.getCustomerById(999)).thenReturn(null);

        Response response = customerController.getCustomer(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Customer not found", response.getEntity());
    }

    @Test
    void testGetCustomer_DatabaseConnectionError() throws SQLException {
        when(customerService.getCustomerById(1))
            .thenThrow(new SQLException("Connection timeout"));

        Response response = customerController.getCustomer(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testGetCustomer_DatabaseSyntaxError() throws SQLException {
        when(customerService.getCustomerById(1))
            .thenThrow(new SQLException("SQL syntax error"));

        Response response = customerController.getCustomer(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testGetCustomer_InvalidCustomerId() throws SQLException {
        when(customerService.getCustomerById(-1)).thenReturn(null);

        Response response = customerController.getCustomer(-1);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Customer not found", response.getEntity());
    }



    @Test
    void testGetAllCustomers_Success() throws SQLException {
        List<Customer> customers = Arrays.asList(validCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        Response response = customerController.getAllCustomers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(customers, response.getEntity());
    }


    @Test
    void testGetAllCustomers_DatabaseConnectionError() throws SQLException {
        when(customerService.getAllCustomers())
            .thenThrow(new SQLException("Connection failed"));

        Response response = customerController.getAllCustomers();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testGetAllCustomers_DatabaseTimeout() throws SQLException {
        when(customerService.getAllCustomers())
            .thenThrow(new SQLException("Query timeout"));

        Response response = customerController.getAllCustomers();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testGetAllCustomers_EmptyResult() throws SQLException {
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList());

        Response response = customerController.getAllCustomers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).isEmpty());
    }

    @Test
    void testGetAllCustomers_DatabasePermissionError() throws SQLException {
        when(customerService.getAllCustomers())
            .thenThrow(new SQLException("Access denied"));

        Response response = customerController.getAllCustomers();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

  

    @Test
    void testUpdateCustomer_Success() throws SQLException, ValidationException {
        when(customerService.updateCustomer(any(Customer.class))).thenReturn(true);

        Response response = customerController.updateCustomer(1, validCustomer);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Customer updated successfully!", response.getEntity());
    }



    @Test
    void testUpdateCustomer_ValidationFailure_Name() throws SQLException, ValidationException {
        when(customerService.updateCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Name can only contain letters, spaces, and .'- characters"));

        Response response = customerController.updateCustomer(1, invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Name can only contain letters, spaces, and .'- characters"));
    }

    @Test
    void testUpdateCustomer_ValidationFailure_Aadhar() throws SQLException, ValidationException {
        when(customerService.updateCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Aadhar number must be 12 digits"));

        Response response = customerController.updateCustomer(1, invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Aadhar number must be 12 digits"));
    }


    @Test
    void testUpdateCustomer_ValidationFailure_Phone() throws SQLException, ValidationException {
        when(customerService.updateCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Phone number must be 10 digits and not start with 0"));

        Response response = customerController.updateCustomer(1, invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Phone number must be 10 digits and not start with 0"));
    }

    @Test
    void testUpdateCustomer_ValidationFailure_Email() throws SQLException, ValidationException {
        when(customerService.updateCustomer(any(Customer.class)))
            .thenThrow(new ValidationException("Invalid email format"));

        Response response = customerController.updateCustomer(1, invalidCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid email format"));
    }

 

    @Test
    void testDeleteCustomer_Success() throws SQLException {
        when(customerService.deleteCustomer(1)).thenReturn(true);

        Response response = customerController.deleteCustomer(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Customer deleted successfully!", response.getEntity());
    }

   
    @Test
    void testDeleteCustomer_NotFound() throws SQLException {
        when(customerService.deleteCustomer(999)).thenReturn(false);

        Response response = customerController.deleteCustomer(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Customer not found", response.getEntity());
    }

    @Test
    void testDeleteCustomer_ForeignKeyConstraint() throws SQLException {
        when(customerService.deleteCustomer(1))
            .thenThrow(new SQLException("Cannot delete due to foreign key constraint"));

        Response response = customerController.deleteCustomer(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testDeleteCustomer_DatabaseConnectionError() throws SQLException {
        when(customerService.deleteCustomer(1))
            .thenThrow(new SQLException("Database connection lost"));

        Response response = customerController.deleteCustomer(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }

    @Test
    void testDeleteCustomer_InvalidCustomerId() throws SQLException {
        when(customerService.deleteCustomer(-1)).thenReturn(false);

        Response response = customerController.deleteCustomer(-1);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Customer not found", response.getEntity());
    }

    @Test
    void testDeleteCustomer_DatabasePermissionError() throws SQLException {
        when(customerService.deleteCustomer(1))
            .thenThrow(new SQLException("Delete permission denied"));

        Response response = customerController.deleteCustomer(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database"));
    }
}