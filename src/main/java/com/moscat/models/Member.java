package com.moscat.models;

import java.sql.Date;

/**
 * Model class for cooperative members
 */
public class Member {
    private int id;
    private String memberNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private int age;
    private Date birthDate;
    private String presentAddress;
    private String permanentAddress;
    private String contactNumber;
    private String email;
    private String employer;
    private String employmentStatus; // REGULAR, CONTRACT OF SERVICE, JOB ORDER
    private double grossMonthlyIncome;
    private double avgNetMonthlyIncome;
    private String status; // ACTIVE, DORMANT, CLOSED
    private Date joinDate;
    private Date lastActivityDate;
    private double loanEligibilityAmount;
    
    /**
     * Default constructor
     */
    public Member() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Member ID
     * @param memberNumber Member number
     * @param firstName First name
     * @param middleName Middle name
     * @param lastName Last name
     * @param age Age
     * @param birthDate Birth date
     * @param presentAddress Present address
     * @param permanentAddress Permanent address
     * @param contactNumber Contact number
     * @param email Email address
     * @param employer Employer
     * @param employmentStatus Employment status
     * @param grossMonthlyIncome Gross monthly income
     * @param avgNetMonthlyIncome Average net monthly income
     * @param status Member status
     * @param joinDate Join date
     * @param lastActivityDate Last activity date
     * @param loanEligibilityAmount Loan eligibility amount
     */
    public Member(int id, String memberNumber, String firstName, String middleName, String lastName,
            int age, Date birthDate, String presentAddress, String permanentAddress,
            String contactNumber, String email, String employer, String employmentStatus,
            double grossMonthlyIncome, double avgNetMonthlyIncome, String status,
            Date joinDate, Date lastActivityDate, double loanEligibilityAmount) {
        this.id = id;
        this.memberNumber = memberNumber;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.age = age;
        this.birthDate = birthDate;
        this.presentAddress = presentAddress;
        this.permanentAddress = permanentAddress;
        this.contactNumber = contactNumber;
        this.email = email;
        this.employer = employer;
        this.employmentStatus = employmentStatus;
        this.grossMonthlyIncome = grossMonthlyIncome;
        this.avgNetMonthlyIncome = avgNetMonthlyIncome;
        this.status = status;
        this.joinDate = joinDate;
        this.lastActivityDate = lastActivityDate;
        this.loanEligibilityAmount = loanEligibilityAmount;
    }

    // Getters and Setters
    
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
    
    public Date getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public double getAvgNetMonthlyIncome() {
        return avgNetMonthlyIncome;
    }
    
    public void setAvgNetMonthlyIncome(double avgNetMonthlyIncome) {
        this.avgNetMonthlyIncome = avgNetMonthlyIncome;
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
    
    public double getLoanEligibilityAmount() {
        return loanEligibilityAmount;
    }
    
    public void setLoanEligibilityAmount(double loanEligibilityAmount) {
        this.loanEligibilityAmount = loanEligibilityAmount;
    }
    
    /**
     * Gets the full name of the member
     * 
     * @return Full name (last name, first name middle initial)
     */
    public String getFullName() {
        String middleInitial = "";
        if (middleName != null && !middleName.isEmpty()) {
            middleInitial = " " + middleName.charAt(0) + ".";
        }
        return lastName + ", " + firstName + middleInitial;
    }
    
    /**
     * Checks if the member account is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * Checks if the member account is dormant
     * 
     * @return true if dormant, false otherwise
     */
    public boolean isDormant() {
        return "DORMANT".equals(status);
    }
}
