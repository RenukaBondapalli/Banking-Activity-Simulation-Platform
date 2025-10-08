package com.example.bank.controller;

import com.example.bank.model.Customer;
import com.example.bank.service.CustomerService;
import com.example.bank.service.impl.CustomerServiceImpl;
import com.example.bank.exception.ValidationException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {

    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());
    private CustomerService service = new CustomerServiceImpl();

    @POST
    @Path("/create")
    public Response createCustomer(Customer customer) {
        try {
            boolean success = service.createCustomer(customer);
            if (success) {
                return Response.status(Response.Status.CREATED).entity("Customer created successfully!").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to create customer").build();
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation Error: " + e.getMessage()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/get/{customerId}")
    public Response getCustomer(@PathParam("customerId") int customerId) {
        try {
            Customer customer = service.getCustomerById(customerId);
            if (customer != null) {
                return Response.ok(customer).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/all")
    public Response getAllCustomers() {
        try {
            List<Customer> customers = service.getAllCustomers();
            return Response.ok(customers).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update/{customerId}")
    public Response updateCustomer(@PathParam("customerId") int customerId, Customer customer) {
        try {
            if (customerId != customer.getCustomerId()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Customer ID mismatch").build();
            }
            boolean success = service.updateCustomer(customer);
            if (success) {
                return Response.ok("Customer updated successfully!").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation Error: " + e.getMessage()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/delete/{customerId}")
    public Response deleteCustomer(@PathParam("customerId") int customerId) {
        try {
            boolean success = service.deleteCustomer(customerId);
            if (success) {
                return Response.ok("Customer deleted successfully!").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database Error: " + e.getMessage()).build();
        }
    }
}