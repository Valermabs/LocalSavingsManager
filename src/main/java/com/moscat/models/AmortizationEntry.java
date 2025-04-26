package com.moscat.models;

import java.util.Date;
import java.util.Calendar;

/**
 * Represents an entry in a loan amortization schedule
 */
public class AmortizationEntry {
    
    private int paymentNumber;
    private Date paymentDate;
    private double beginningBalance;
    private double payment;
    private double principalPayment;
    private double interestPayment;
    private double endingBalance;

    /**
     * Default constructor
     */
    public AmortizationEntry() {
    }

    /**
     * Gets the payment number in the sequence
     * 
     * @return Payment number
     */
    public int getPaymentNumber() {
        return paymentNumber;
    }

    /**
     * Sets the payment number in the sequence
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
     * Gets the total payment amount
     * 
     * @return Payment amount
     */
    public double getPayment() {
        return payment;
    }

    /**
     * Sets the total payment amount
     * 
     * @param payment Payment amount
     */
    public void setPayment(double payment) {
        this.payment = payment;
    }

    /**
     * Gets the principal payment amount
     * 
     * @return Principal payment
     */
    public double getPrincipalPayment() {
        return principalPayment;
    }

    /**
     * Sets the principal payment amount
     * 
     * @param principalPayment Principal payment
     */
    public void setPrincipalPayment(double principalPayment) {
        this.principalPayment = principalPayment;
    }

    /**
     * Gets the interest payment amount
     * 
     * @return Interest payment
     */
    public double getInterestPayment() {
        return interestPayment;
    }

    /**
     * Sets the interest payment amount
     * 
     * @param interestPayment Interest payment
     */
    public void setInterestPayment(double interestPayment) {
        this.interestPayment = interestPayment;
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
    
    /**
     * Gets the total monthly payment (principal + interest)
     * 
     * @return Monthly payment
     */
    public double getMonthlyPayment() {
        return payment;
    }
    
    /**
     * Gets the remaining balance after payment
     * 
     * @return Remaining balance
     */
    public double getRemainingBalance() {
        return endingBalance;
    }
    
    /**
     * Gets the year part of the payment date
     * 
     * @return Year of the payment
     */
    public int getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(paymentDate);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * Gets the month part of the payment date
     * 
     * @return Month of the payment (1-12)
     */
    public int getMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(paymentDate);
        return cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based, we add 1 to make it 1-based
    }
}