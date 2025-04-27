package com.moscat.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a cooperative member
 */
public class Member {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private int age;
    private LocalDate birthdate;
    private String presentAddress;
    private String permanentAddress;
    private String contactNumber;
    private String emailAddress;
    private String employer;
    private String employmentStatus;
    private double grossMonthlyIncome;
    private double averageNetMonthlyIncome;
    private double savingsBalance;
    private double interestEarned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    
    // Default constructor
    public Member() {
        this.savingsBalance = 0.0;
        this.interestEarned = 0.0;
        this.status = "Active";
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public LocalDate getBirthdate() {
        return birthdate;
    }
    
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    
    public String getPresentAddress() {
        return presentAddress;
    }
    
    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }
    
    public String getPermanentAddress() {
        return permanentAddress;
    }
    
    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public String getEmployer() {
        return employer;
    }
    
    public void setEmployer(String employer) {
        this.employer = employer;
    }
    
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    public double getGrossMonthlyIncome() {
        return grossMonthlyIncome;
    }
    
    public void setGrossMonthlyIncome(double grossMonthlyIncome) {
        this.grossMonthlyIncome = grossMonthlyIncome;
    }
    
    public double getAverageNetMonthlyIncome() {
        return averageNetMonthlyIncome;
    }
    
    public void setAverageNetMonthlyIncome(double averageNetMonthlyIncome) {
        this.averageNetMonthlyIncome = averageNetMonthlyIncome;
    }
    
    public double getSavingsBalance() {
        return savingsBalance;
    }
    
    public void setSavingsBalance(double savingsBalance) {
        this.savingsBalance = savingsBalance;
    }
    
    public double getInterestEarned() {
        return interestEarned;
    }
    
    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the full name of the member
     * 
     * @return Full name in "Last, First Middle" format
     */
    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return lastName + ", " + firstName + " " + middleName;
        } else {
            return lastName + ", " + firstName;
        }
    }
    
    /**
     * Check if account is dormant
     * 
     * @return True if the account is dormant, false otherwise
     */
    public boolean isDormant() {
        return "Dormant".equals(status);
    }
}