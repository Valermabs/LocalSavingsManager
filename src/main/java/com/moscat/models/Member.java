package com.moscat.models;

import java.util.Date;

/**
 * Model for member data
 */
public class Member {
    
    private int id;
    private String memberId;
    private String memberNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private double loanEligibilityAmount;
    private String address;
    private String presentAddress;
    private String permanentAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String contactNumber;
    private String email;
    private String idType;
    private String idNumber;
    private String occupation;
    private String employer;
    private String employmentStatus;
    private double monthlyIncome;
    private double grossMonthlyIncome;
    private double avgNetMonthlyIncome;
    private String status;
    private Date joinDate;
    private Date lastActivityDate;
    private Date createdAt;
    private Date updatedAt;
    private String notes;
    
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
     * Gets the member ID
     * 
     * @return Member ID
     */
    public String getMemberId() {
        return memberId;
    }
    
    /**
     * Sets the member ID
     * 
     * @param memberId Member ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
     * Gets the gender
     * 
     * @return Gender
     */
    public String getGender() {
        return gender;
    }
    
    /**
     * Sets the gender
     * 
     * @param gender Gender
     */
    public void setGender(String gender) {
        this.gender = gender;
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
     * Calculates the member's age based on birth date
     * 
     * @return Age in years
     */
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        
        java.util.Calendar birthCal = java.util.Calendar.getInstance();
        birthCal.setTime(birthDate);
        
        java.util.Calendar currentCal = java.util.Calendar.getInstance();
        
        int age = currentCal.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR);
        
        // Adjust age if birthday hasn't occurred yet this year
        if (birthCal.get(java.util.Calendar.MONTH) > currentCal.get(java.util.Calendar.MONTH) || 
                (birthCal.get(java.util.Calendar.MONTH) == currentCal.get(java.util.Calendar.MONTH) && 
                 birthCal.get(java.util.Calendar.DAY_OF_MONTH) > currentCal.get(java.util.Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age;
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
     * Gets the address
     * 
     * @return Address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the address
     * 
     * @param address Address
     */
    public void setAddress(String address) {
        this.address = address;
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
     * Gets the city
     * 
     * @return City
     */
    public String getCity() {
        return city;
    }
    
    /**
     * Sets the city
     * 
     * @param city City
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**
     * Gets the state
     * 
     * @return State
     */
    public String getState() {
        return state;
    }
    
    /**
     * Sets the state
     * 
     * @param state State
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**
     * Gets the postal code
     * 
     * @return Postal code
     */
    public String getPostalCode() {
        return postalCode;
    }
    
    /**
     * Sets the postal code
     * 
     * @param postalCode Postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    /**
     * Gets the country
     * 
     * @return Country
     */
    public String getCountry() {
        return country;
    }
    
    /**
     * Sets the country
     * 
     * @param country Country
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Gets the phone
     * 
     * @return Phone
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the phone
     * 
     * @param phone Phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
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
     * Gets the ID type
     * 
     * @return ID type
     */
    public String getIdType() {
        return idType;
    }
    
    /**
     * Sets the ID type
     * 
     * @param idType ID type
     */
    public void setIdType(String idType) {
        this.idType = idType;
    }
    
    /**
     * Gets the ID number
     * 
     * @return ID number
     */
    public String getIdNumber() {
        return idNumber;
    }
    
    /**
     * Sets the ID number
     * 
     * @param idNumber ID number
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    
    /**
     * Gets the occupation
     * 
     * @return Occupation
     */
    public String getOccupation() {
        return occupation;
    }
    
    /**
     * Sets the occupation
     * 
     * @param occupation Occupation
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
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
     * Gets the monthly income
     * 
     * @return Monthly income
     */
    public double getMonthlyIncome() {
        return monthlyIncome;
    }
    
    /**
     * Sets the monthly income
     * 
     * @param monthlyIncome Monthly income
     */
    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
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
     * Gets the created at date
     * 
     * @return Created at date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the created at date
     * 
     * @param createdAt Created at date
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the updated at date
     * 
     * @return Updated at date
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the updated at date
     * 
     * @param updatedAt Updated at date
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the notes
     * 
     * @return Notes
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Sets the notes
     * 
     * @param notes Notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Gets the full name
     * 
     * @return Full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Gets the full address
     * 
     * @return Full address
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (address != null && !address.isEmpty()) {
            sb.append(address);
        }
        
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(city);
        }
        
        if (state != null && !state.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(state);
        }
        
        if (postalCode != null && !postalCode.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(postalCode);
        }
        
        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(country);
        }
        
        return sb.toString();
    }
    
    /**
     * Checks if the member is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return "Active".equals(status);
    }
    
    /**
     * Checks if the member is inactive
     * 
     * @return true if inactive, false otherwise
     */
    public boolean isInactive() {
        return "Inactive".equals(status);
    }
    
    /**
     * Checks if the member is pending
     * 
     * @return true if pending, false otherwise
     */
    public boolean isPending() {
        return "Pending".equals(status);
    }
}