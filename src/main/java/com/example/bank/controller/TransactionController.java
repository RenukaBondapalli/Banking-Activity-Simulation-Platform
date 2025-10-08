package com.example.bank.controller;

import com.example.bank.model.Transaction;
import com.example.bank.service.TransactionService;
import com.example.bank.service.impl.TransactionServiceImpl;
import com.example.bank.exception.ValidationException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {

    private static final Logger logger = Logger.getLogger(TransactionController.class.getName());
    private TransactionService transactionService = new TransactionServiceImpl();

    @POST
    @Path("/deposit")
    public Response deposit(Transaction transaction) {
        try {
            logger.info("Received deposit request: " + transaction.getUtrNumber());
            
            boolean success = transactionService.deposit(transaction);
            
            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity("Deposit successful! UTR: " + transaction.getUtrNumber())
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Deposit failed")
                        .build();
            }
            
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation Error: " + e.getMessage())
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/withdraw")
    public Response withdraw(Transaction transaction) {
        try {
            logger.info("Received withdrawal request: " + transaction.getUtrNumber());
            
            boolean success = transactionService.withdraw(transaction);
            
            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity("Withdrawal successful! UTR: " + transaction.getUtrNumber())
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Withdrawal failed")
                        .build();
            }
            
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation Error: " + e.getMessage())
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/account/{accountNumber}")
    public Response getTransactionsByAccountNumber(@PathParam("accountNumber") String accountNumber) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
            
            if (transactions != null && !transactions.isEmpty()) {
                return Response.ok(transactions).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No transactions found for account: " + accountNumber)
                        .build();
            }
            
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }


    @POST
    @Path("/transfer")
    public Response transfer(Transaction transaction) {
        try {
            boolean success = transactionService.transfer(transaction);

            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity("Transfer successful from " + transaction.getSenderAccountNumber() +
                                " to " + transaction.getReceiverAccountNumber())
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Transfer failed")
                        .build();
            }

        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation Error: " + e.getMessage())
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database Error: " + e.getMessage())
                    .build();
        }
    }
    

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("Transactions API is working!").build();
    }
}