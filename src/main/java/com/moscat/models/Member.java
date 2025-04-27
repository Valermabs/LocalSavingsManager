package com.moscat.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents a cooperative member
 */
public class Member {
    private int id;
    private String memberNumber; // Member's unique identifier number
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
    private Date joinDate;        // Date when member joined the cooperative
    private Date lastActivityDate; // Date of last account activity
    
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
    
    public String getMemberNumber() {
        return memberNumber;
    }
    
    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
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
    
    public Date getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
    
    public Date getLastActivityDate() {
        return lastActivityDate;
    }
    
    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
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
    
    /**
     * Check if account is active
     * 
     * @return True if the account is active, false otherwise
     */
    public boolean isActive() {
        return "Active".equals(status);
    }
    
    /**
     * Check if account is inactive
     * 
     * @return True if the account is inactive, false otherwise
     */
    public boolean isInactive() {
        return "Inactive".equals(status);
    }
    
    /**
     * Check if member application is pending
     * 
     * @return True if the member application is pending, false otherwise
     */
    public boolean isPending() {
        return "Pending".equals(status);
    }
    
    // Alias methods for compatibility with views
    
    /**
     * Alias for getEmailAddress()
     */
    public String getEmail() {
        return getEmailAddress();
    }
    
    /**
     * Alias for setEmailAddress()
     */
    public void setEmail(String email) {
        setEmailAddress(email);
    }
    
    /**
     * Alias for getAverageNetMonthlyIncome()
     */
    public double getAvgNetMonthlyIncome() {
        return getAverageNetMonthlyIncome();
    }
    
    /**
     * Alias for setAverageNetMonthlyIncome()
     */
    public void setAvgNetMonthlyIncome(double income) {
        setAverageNetMonthlyIncome(income);
    }
    
    /**
     * Alias for getBirthdate() to fix casing inconsistency
     * 
     * @return Member's birth date
     */
    public Date getBirthDate() {
        if (birthdate != null) {
            return com.moscat.utils.DateUtils.convertToDate(birthdate);
        }
        return null;
    }
    
    /**
     * Alias for setBirthdate() to fix casing inconsistency
     * 
     * @param date Member's birth date
     */
    public void setBirthDate(Date date) {
        if (date != null) {
            this.birthdate = com.moscat.utils.DateUtils.convertToLocalDate(date);
        }
    }
}