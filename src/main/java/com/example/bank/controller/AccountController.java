package com.example.bank.controller;

import com.example.bank.model.Account;
import com.example.bank.service.AccountService;
import com.example.bank.service.impl.AccountServiceImpl;
import com.example.bank.exception.ValidationException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {

    private static final Logger logger = Logger.getLogger(AccountController.class.getName());
    private AccountService service = new AccountServiceImpl();

    @POST
    @Path("/create")
    public Response createAccount(Account account) {
        try {
            logger.info("Controller: Received account creation request: " + account.getAccountNumber());
            
            boolean success = service.createAccount(account);
            
            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity("Account Created Successfully!")
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Failed to create account")
                        .build();
            }
            
        } catch (ValidationException e) {
            logger.warning("Controller: Validation Error: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation Error: " + e.getMessage())
                    .build();
        } catch (SQLException e) {
            logger.severe("Controller: Database Error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/get/{accountNumber}")
    public Response getAccount(@PathParam("accountNumber") String accountNumber) {
        try {
            logger.info("Controller: GET request for account: " + accountNumber);
            
            Account account = service.getAccountByNumber(accountNumber);
            
            if (account != null) {
                logger.info("Controller: Account found: " + accountNumber);
                return Response.ok(account).build();
            } else {
                logger.warning("Controller: Account not found: " + accountNumber);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Account not found: " + accountNumber)
                        .build();
            }
            
        } catch (SQLException e) {
            logger.severe("Controller: Database Error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/all")
    public Response getAllAccounts() {
        try {
            logger.info("Controller: GET all accounts request");
            
            List<Account> accounts = service.getAllAccounts();
            
            if (accounts != null && !accounts.isEmpty()) {
                logger.info("Controller: Returned " + accounts.size() + " accounts");
                return Response.ok(accounts).build();
            } else {
                logger.warning("Controller: No accounts found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No accounts found")
                        .build();
            }
            
        } catch (SQLException e) {
            logger.severe("Controller: Database Error getting all accounts: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/update/{accountNumber}")
    public Response updateAccount(@PathParam("accountNumber") String accountNumber, Account account) {
        try {
            logger.info("Controller: PUT request for account: " + accountNumber);
            
            if (!accountNumber.equals(account.getAccountNumber())) {
                logger.warning("Controller: Account number mismatch in update");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Account number in path does not match request body")
                        .build();
            }
            
            boolean success = service.updateAccount(account);
            
            if (success) {
                logger.info("Controller: Account updated successfully: " + accountNumber);
                return Response.ok("Account updated successfully!").build();
            } else {
                logger.warning("Controller: Account not found for update: " + accountNumber);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Account not found: " + accountNumber)
                        .build();
            }
            
        } catch (ValidationException e) {
            logger.warning("Controller: Validation Error during update: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation Error: " + e.getMessage())
                    .build();
        } catch (SQLException e) {
            logger.severe("Controller: Database Error updating account: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }
        @DELETE
        @Path("/delete/{accountNumber}")
        public Response deleteAccount(@PathParam("accountNumber") String accountNumber) {
            try {
                logger.info("Controller: DELETE request for account: " + accountNumber);
                
                boolean success = service.deleteAccount(accountNumber);
                
                if (success) {
                    logger.info("Controller: Account deleted successfully: " + accountNumber);
                    return Response.ok("Account deleted successfully!").build();
                } else {
                    logger.warning("Controller: Account not found for deletion: " + accountNumber);
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Account not found: " + accountNumber)
                            .build();
                }
                
            } catch (SQLException e) {
                logger.severe("Controller: Database Error deleting account: " + e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Database Error: " + e.getMessage())
                        .build();
            }
        }


    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("Bank API is working!").build();
    }

    @POST
    @Path("/simple")
    public Response simpleTest() {
        return Response.ok("Simple POST works!").build();
    }
}

