package com.example.bank.app;

import com.example.bank.model.Customer;
import com.example.bank.model.Account;
import com.example.bank.model.Transaction;

public class App {
    public static void main(String[] args) {
        Customer.createTable();
        Account.createTable();
        Transaction.createTable();
    }
}

