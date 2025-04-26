package com.moscat.models;

import java.util.Date;

/**
 * Model for member data
 */
public class Member {
    
    private int id;
    private String memberNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date birthDate;
    private String contactNumber;
    private String email;
    private String presentAddress;
    private String permanentAddress;
    private String employer;
    private String employmentStatus;
    private double grossMonthlyIncome;
    private double avgNetMonthlyIncome;
    private String status;
    private Date joinDate;
    private Date lastActivityDate;
    private double loanEligibilityAmount;
    
    /**
     * Default constructor
     */
    public Member() {
    }
    
    /**
     * Gets the ID
     * 
     * @return ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the ID
     * 
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the member number
     * 
     * @return Member number
     */
    public String getMemberNumber() {
        return memberNumber;
    }
    
    /**
     * Sets the member number
     * 
     * @param memberNumber Member number
     */
    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }
    
    /**
     * Gets the first name
     * 
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name
     * 
     * @param firstName First name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the middle name
     * 
     * @return Middle name
     */
    public String getMiddleName() {
        return middleName;
    }
    
    /**
     * Sets the middle name
     * 
     * @param middleName Middle name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    /**
     * Gets the last name
     * 
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name
     * 
     * @param lastName Last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the birth date
     * 
     * @return Birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }
    
    /**
     * Sets the birth date
     * 
     * @param birthDate Birth date
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
    /**
     * Gets the contact number
     * 
     * @return Contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }
    
    /**
     * Sets the contact number
     * 
     * @param contactNumber Contact number
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    /**
     * Gets the email
     * 
     * @return Email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email
     * 
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the present address
     * 
     * @return Present address
     */
    public String getPresentAddress() {
        return presentAddress;
    }
    
    /**
     * Sets the present address
     * 
     * @param presentAddress Present address
     */
    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }
    
    /**
     * Gets the permanent address
     * 
     * @return Permanent address
     */
    public String getPermanentAddress() {
        return permanentAddress;
    }
    
    /**
     * Sets the permanent address
     * 
     * @param permanentAddress Permanent address
     */
    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }
    
    /**
     * Gets the employer
     * 
     * @return Employer
     */
    public String getEmployer() {
        return employer;
    }
    
    /**
     * Sets the employer
     * 
     * @param employer Employer
     */
    public void setEmployer(String employer) {
        this.employer = employer;
    }
    
    /**
     * Gets the employment status
     * 
     * @return Employment status
     */
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    /**
     * Sets the employment status
     * 
     * @param employmentStatus Employment status
     */
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    /**
     * Gets the gross monthly income
     * 
     * @return Gross monthly income
     */
    public double getGrossMonthlyIncome() {
        return grossMonthlyIncome;
    }
    
    /**
     * Sets the gross monthly income
     * 
     * @param grossMonthlyIncome Gross monthly income
     */
    public void setGrossMonthlyIncome(double grossMonthlyIncome) {
        this.grossMonthlyIncome = grossMonthlyIncome;
    }
    
    /**
     * Gets the average net monthly income
     * 
     * @return Average net monthly income
     */
    public double getAvgNetMonthlyIncome() {
        return avgNetMonthlyIncome;
    }
    
    /**
     * Sets the average net monthly income
     * 
     * @param avgNetMonthlyIncome Average net monthly income
     */
    public void setAvgNetMonthlyIncome(double avgNetMonthlyIncome) {
        this.avgNetMonthlyIncome = avgNetMonthlyIncome;
    }
    
    /**
     * Gets the status
     * 
     * @return Status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the status
     * 
     * @param status Status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the join date
     * 
     * @return Join date
     */
    public Date getJoinDate() {
        return joinDate;
    }
    
    /**
     * Sets the join date
     * 
     * @param joinDate Join date
     */
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
    
    /**
     * Gets the last activity date
     * 
     * @return Last activity date
     */
    public Date getLastActivityDate() {
        return lastActivityDate;
    }
    
    /**
     * Sets the last activity date
     * 
     * @param lastActivityDate Last activity date
     */
    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    /**
     * Gets the loan eligibility amount
     * 
     * @return Loan eligibility amount
     */
    public double getLoanEligibilityAmount() {
        return loanEligibilityAmount;
    }
    
    /**
     * Sets the loan eligibility amount
     * 
     * @param loanEligibilityAmount Loan eligibility amount
     */
    public void setLoanEligibilityAmount(double loanEligibilityAmount) {
        this.loanEligibilityAmount = loanEligibilityAmount;
    }
    
    /**
     * Gets the full name of the member
     * 
     * @return Full name
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        return sb.toString();
    }
}