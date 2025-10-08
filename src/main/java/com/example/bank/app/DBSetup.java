package com.example.bank.app;

import com.example.bank.dao.CustomerDAO;
import com.example.bank.dao.AccountDAO;
import com.example.bank.dao.TransactionDAO;

public class DBSetup {
    public static void main(String[] args) {

        CustomerDAO customerDAO = new CustomerDAO();
        customerDAO.createCustomerTable();
        System.out.println("Customer table created.");

        AccountDAO accountDAO = new AccountDAO();
        accountDAO.createAccountTable();
        System.out.println("Account table created.");

        TransactionDAO transactionDAO = new TransactionDAO();
        transactionDAO.createTransactionTable();
        System.out.println("Transaction table created.");

    }
}

