package com.moscat.models;

import java.util.Date;

/**
 * Model class for loan amortization schedule entries
 */
public class AmortizationEntry {
    private int paymentNumber;
    private Date paymentDate;
    private double beginningBalance;
    private double payment;
    private double interestPayment;
    private double principalPayment;
    private double endingBalance;
    
    /**
     * Default constructor
     */
    public AmortizationEntry() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param paymentNumber Payment number
     * @param paymentDate Payment date
     * @param beginningBalance Beginning balance
     * @param payment Total payment amount
     * @param interestPayment Interest portion of payment
     * @param principalPayment Principal portion of payment
     * @param endingBalance Ending balance
     */
    public AmortizationEntry(int paymentNumber, Date paymentDate, double beginningBalance,
            double payment, double interestPayment, double principalPayment, double endingBalance) {
        this.paymentNumber = paymentNumber;
        this.paymentDate = paymentDate;
        this.beginningBalance = beginningBalance;
        this.payment = payment;
        this.interestPayment = interestPayment;
        this.principalPayment = principalPayment;
        this.endingBalance = endingBalance;
    }
    
    /**
     * Gets the payment number
     * 
     * @return Payment number
     */
    public int getPaymentNumber() {
        return paymentNumber;
    }
    
    /**
     * Sets the payment number
     * 
     * @param paymentNumber Payment number
     */
    public void setPaymentNumber(int paymentNumber) {
        this.paymentNumber = paymentNumber;
    }
    
    /**
     * Gets the payment date
     * 
     * @return Payment date
     */
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    /**
     * Sets the payment date
     * 
     * @param paymentDate Payment date
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    /**
     * Gets the beginning balance
     * 
     * @return Beginning balance
     */
    public double getBeginningBalance() {
        return beginningBalance;
    }
    
    /**
     * Sets the beginning balance
     * 
     * @param beginningBalance Beginning balance
     */
    public void setBeginningBalance(double beginningBalance) {
        this.beginningBalance = beginningBalance;
    }
    
    /**
     * Gets the payment amount
     * 
     * @return Payment amount
     */
    public double getPayment() {
        return payment;
    }
    
    /**
     * Sets the payment amount
     * 
     * @param payment Payment amount
     */
    public void setPayment(double payment) {
        this.payment = payment;
    }
    
    /**
     * Gets the interest portion of payment
     * 
     * @return Interest payment amount
     */
    public double getInterestPayment() {
        return interestPayment;
    }
    
    /**
     * Sets the interest portion of payment
     * 
     * @param interestPayment Interest payment amount
     */
    public void setInterestPayment(double interestPayment) {
        this.interestPayment = interestPayment;
    }
    
    /**
     * Gets the principal portion of payment
     * 
     * @return Principal payment amount
     */
    public double getPrincipalPayment() {
        return principalPayment;
    }
    
    /**
     * Sets the principal portion of payment
     * 
     * @param principalPayment Principal payment amount
     */
    public void setPrincipalPayment(double principalPayment) {
        this.principalPayment = principalPayment;
    }
    
    /**
     * Gets the ending balance
     * 
     * @return Ending balance
     */
    public double getEndingBalance() {
        return endingBalance;
    }
    
    /**
     * Sets the ending balance
     * 
     * @param endingBalance Ending balance
     */
    public void setEndingBalance(double endingBalance) {
        this.endingBalance = endingBalance;
    }
}