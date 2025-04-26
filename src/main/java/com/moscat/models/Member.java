package com.moscat.models;

import java.util.Date;

/**
 * Model for member data
 */
public class Member {
    
    private int id;
    private String memberId;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String idType;
    private String idNumber;
    private String occupation;
    private String employer;
    private double monthlyIncome;
    private String status;
    private Date joinDate;
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
}