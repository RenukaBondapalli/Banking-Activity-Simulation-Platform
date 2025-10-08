package com.example.bank.util; 

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[1-9][0-9]{9}$");
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s.'-]{2,50}$");
    
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{8,20}$");
    
    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }

    public static boolean isValidIFSC(String ifscCode) {
        return ifscCode != null && IFSC_PATTERN.matcher(ifscCode).matches();
    }

    public static boolean isValidBalance(BigDecimal balance) {
        return balance != null && balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private static final Pattern AADHAR_PATTERN = Pattern.compile("^[0-9]{12}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");

    public static boolean isValidAadhar(String aadhar) {
        return aadhar != null && AADHAR_PATTERN.matcher(aadhar).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}