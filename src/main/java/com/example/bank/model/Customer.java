package com.example.bank.model;

import java.sql.Date;
import java.time.LocalDateTime;
import jakarta.json.bind.annotation.JsonbDateFormat;

public class Customer {
    private int customerId;
    private String customerName;
    private String username;
    private String password;
    private String aadharNumber;
    private String permanentAddress;
    private String state;
    private String country;
    private String city;
    private String email;
    private String phoneNumber;
    private String status;
    private String pin;
    
    @JsonbDateFormat("yyyy-MM-dd")
    private Date dob; 
    
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")  
    private LocalDateTime modifiedOn;
    
    private int age;
    private String gender;
    private String fatherName;
    private String motherName;

    public Customer() {}

    public Customer(int customerId, String customerName, String username, String password, 
                   String aadharNumber, String permanentAddress, String state, String country, 
                   String city, String email, String phoneNumber, String status, Date dob, 
                   int age, String gender, String fatherName, String motherName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.username = username;
        this.password = password;
        this.aadharNumber = aadharNumber;
        this.permanentAddress = permanentAddress;
        this.state = state;
        this.country = country;
        this.city = city;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.dob = dob;
        this.age = age;
        this.gender = gender;
        this.fatherName = fatherName;
        this.motherName = motherName;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }

    public LocalDateTime getModifiedOn() { return modifiedOn; }
    public void setModifiedOn(LocalDateTime modifiedOn) { this.modifiedOn = modifiedOn; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getPin() { return pin; }

    public void setPin(String pin) { this.pin = pin; }
}
