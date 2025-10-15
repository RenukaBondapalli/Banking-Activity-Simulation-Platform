package com.example.bank.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private String utrNumber;
    private Timestamp transactionDate;
    private BigDecimal transactionAmount;
    private int accountId;
    private BigDecimal balanceAmount;
    private String description;
    private String receiver;
    private String transactionType;
    private String modeOfTransaction;
    private String senderAccountNumber;
    private String receiverAccountNumber; 
    private String accountNumber;
    private String pin;

   
    public Transaction() {}

    public Transaction(String utrNumber, BigDecimal transactionAmount, int accountId, 
                     BigDecimal balanceAmount, String description, String transactionType, 
                     String modeOfTransaction, String receiver) {
        this.utrNumber = utrNumber;
        this.transactionAmount = transactionAmount;
        this.accountId = accountId;
        this.balanceAmount = balanceAmount;
        this.description = description;
        this.transactionType = transactionType;
        this.modeOfTransaction = modeOfTransaction;
        this.receiver = receiver;
    }

   
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getModeOfTransaction() { return modeOfTransaction; }
    public void setModeOfTransaction(String modeOfTransaction) { this.modeOfTransaction = modeOfTransaction; }

    public String getSenderAccountNumber() {
    return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }

}
