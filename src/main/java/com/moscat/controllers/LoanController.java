package com.moscat.controllers;

import com.moscat.models.Loan;
import com.moscat.models.LoanAmortization;
import com.moscat.models.LoanDeduction;
import com.moscat.models.LoanType;
import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.models.AmortizationEntry;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Controller for loan operations
 */
public class LoanController {
    
    /**
     * Calculates a loan based on provided parameters
     * 
     * @param memberId Member ID
     * @param loanType Loan type
     * @param principalAmount Principal amount
     * @param termYears Term in years
     * @return Calculated loan object
     */
    public static Loan calculateLoan(int memberId, String loanType, double principalAmount, int termYears) {
        Loan loan = new Loan();
        loan.setMemberId(memberId);
        loan.setLoanType(loanType);
        loan.setPrincipalAmount(principalAmount);
        loan.setTermYears(termYears);
        
        // Default interest rate based on loan type
        double interestRate;
        switch (loanType) {
            case Constants.LOAN_TYPE_EMERGENCY:
                interestRate = 10.0; // 10%
                break;
            case Constants.LOAN_TYPE_EDUCATIONAL:
                interestRate = 8.0; // 8%
                break;
            case Constants.LOAN_TYPE_BUSINESS:
                interestRate = 12.0; // 12%
                break;
            default: // Personal and others
                interestRate = 12.0; // 12%
        }
        loan.setInterestRate(interestRate);
        
        // Calculate amortization schedule
        loan.calculateAmortizationSchedule();
        
        return loan;
    }
    
    /**
     * Submits a loan application
     * 
     * @param loan Loan object with application details
     * @return True if application submitted successfully, false otherwise
     */
    public static boolean applyForLoan(Loan loan) {
        int loanId = createLoanApplication(loan);
        return loanId > 0;
    }
    
    /**
     * Gets all loans from the database
     * 
     * @return List of all loans
     */
    public static List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            String query = "SELECT * FROM loans ORDER BY application_date DESC";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResources(rs, stmt, conn);
        }
        
        return loans;
    }
    
    /**
     * Searches for loans based on a search term
     * 
     * @param searchTerm Search term (loan number, member name, etc.)
     * @return List of matching loans
     */
    public static List<Loan> searchLoans(String searchTerm) {
        List<Loan> loans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            String query = "SELECT l.* FROM loans l " +
                "JOIN members m ON l.member_id = m.id " +
                "WHERE l.loan_number LIKE ? " +
                "OR m.first_name LIKE ? " +
                "OR m.last_name LIKE ? " +
                "OR CONCAT(m.first_name, ' ', m.last_name) LIKE ? " +
                "ORDER BY l.application_date DESC";
            
            stmt = conn.prepareStatement(query);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResources(rs, stmt, conn);
        }
        
        return loans;
    }
    
    /**
     * Gets the amortization schedule for a loan
     * 
     * @param loan Loan to calculate schedule for
     * @return List of amortization entries
     */
    public static List<AmortizationEntry> getAmortizationSchedule(Loan loan) {
        List<AmortizationEntry> schedule = new ArrayList<>();
        
        // Only create schedule if loan is active or approved
        if (loan == null || (!loan.isActive() && !loan.isApproved())) {
            return schedule;
        }
        
        double monthlyInterestRate = loan.getInterestRate() / 100 / 12;
        int numberOfPayments = loan.getTermYears() * 12;
        double loanAmount = loan.getPrincipalAmount();
        
        // Calculate monthly payment using the formula: P = (r*PV) / (1 - (1+r)^-n)
        double monthlyPayment = (monthlyInterestRate * loanAmount) / 
                (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments));
        
        // Set start date to release date if available, otherwise use current date
        Calendar calendar = Calendar.getInstance();
        if (loan.getReleaseDate() != null) {
            calendar.setTime(loan.getReleaseDate());
        }
        
        double remainingBalance = loanAmount;
        
        for (int i = 1; i <= numberOfPayments; i++) {
            double interestPayment = remainingBalance * monthlyInterestRate;
            double principalPayment = monthlyPayment - interestPayment;
            double endingBalance = remainingBalance - principalPayment;
            
            if (endingBalance < 0.01) {
                endingBalance = 0; // Avoid tiny floating point balance at the end
            }
            
            AmortizationEntry entry = new AmortizationEntry();
            entry.setPaymentNumber(i);
            entry.setPaymentDate((java.util.Date) calendar.getTime().clone());
            entry.setBeginningBalance(remainingBalance);
            entry.setPayment(monthlyPayment);
            entry.setInterestPayment(interestPayment);
            entry.setPrincipalPayment(principalPayment);
            entry.setEndingBalance(endingBalance);
            
            schedule.add(entry);
            
            // Advance to next month
            calendar.add(Calendar.MONTH, 1);
            
            // Update remaining balance
            remainingBalance = endingBalance;
        }
        
        return schedule;
    }
    
    /**
     * Processes a loan payment
     * 
     * @param loanNumber Loan number
     * @param amount Payment amount
     * @param notes Payment notes
     * @return True if payment processed successfully, false otherwise
     */
    public static boolean processLoanPayment(String loanNumber, double amount, String notes) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean success = false;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // First, get the loan details
            Loan loan = getLoanByNumber(loanNumber);
            if (loan == null || !loan.isActive()) {
                // Loan not found or not active
                return false;
            }
            
            // Calculate remaining balance after payment
            double newBalance = loan.getRemainingBalance() - amount;
            if (newBalance < 0) {
                newBalance = 0;
            }
            
            // Update loan remaining balance and last payment date
            String updateQuery = "UPDATE loans SET remaining_balance = ?, last_payment_date = ? WHERE loan_number = ?";
            stmt = conn.prepareStatement(updateQuery);
            stmt.setDouble(1, newBalance);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, loanNumber);
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                // Insert payment record
                String insertQuery = "INSERT INTO loan_payments (loan_id, payment_date, amount, notes) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(insertQuery);
                stmt.setInt(1, loan.getId());
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setDouble(3, amount);
                stmt.setString(4, notes);
                
                stmt.executeUpdate();
                
                // If balance is 0, update status to PAID
                if (newBalance == 0) {
                    String statusQuery = "UPDATE loans SET status = ? WHERE loan_number = ?";
                    stmt = conn.prepareStatement(statusQuery);
                    stmt.setString(1, Constants.LOAN_STATUS_COMPLETED);
                    stmt.setString(2, loanNumber);
                    stmt.executeUpdate();
                }
                
                conn.commit();
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseManager.closeResources(rs, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Approves a loan application
     * 
     * @param loanNumber Loan number to approve
     * @return True if approved successfully, false otherwise
     */
    public static boolean approveLoan(String loanNumber) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "UPDATE loans SET status = ?, approval_date = ? WHERE loan_number = ? AND status = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, Constants.LOAN_STATUS_APPROVED);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, loanNumber);
            stmt.setString(4, Constants.LOAN_STATUS_PENDING);
            
            int rowsUpdated = stmt.executeUpdate();
            success = rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Rejects a loan application
     * 
     * @param loanNumber Loan number to reject
     * @return True if rejected successfully, false otherwise
     */
    public static boolean rejectLoan(String loanNumber) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "UPDATE loans SET status = ?, approval_date = ? WHERE loan_number = ? AND status = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, Constants.LOAN_STATUS_REJECTED);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, loanNumber);
            stmt.setString(4, Constants.LOAN_STATUS_PENDING);
            
            int rowsUpdated = stmt.executeUpdate();
            success = rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Gets a loan by its loan number
     * 
     * @param loanNumber Loan number to find
     * @return Loan object if found, null otherwise
     */
    public static Loan getLoanByNumber(String loanNumber) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Loan loan = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            String query = "SELECT * FROM loans WHERE loan_number = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, loanNumber);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                loan = extractLoanFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResources(rs, stmt, conn);
        }
        
        return loan;
    }
    
    /**
     * Gets a loan by its ID
     * 
     * @param loanId Loan ID to find
     * @return Loan object if found, null otherwise
     */
    public static Loan getLoanById(int loanId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Loan loan = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            String query = "SELECT * FROM loans WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, loanId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                loan = extractLoanFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResources(rs, stmt, conn);
        }
        
        return loan;
    }
    
    /**
     * Extracts a Loan object from a ResultSet
     * 
     * @param rs ResultSet containing loan data
     * @return Populated Loan object
     * @throws SQLException If there's an error extracting data
     */
    private static Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setMemberId(rs.getInt("member_id"));
        loan.setLoanNumber(rs.getString("loan_number"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setPrincipalAmount(rs.getDouble("principal_amount"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setTermYears(rs.getInt("term_years"));
        loan.setPreviousLoanBalance(rs.getDouble("previous_loan_balance"));
        loan.setTotalDeductions(rs.getDouble("total_deductions"));
        loan.setNetLoanProceeds(rs.getDouble("net_loan_proceeds"));
        loan.setMonthlyAmortization(rs.getDouble("monthly_amortization"));
        loan.setRemainingBalance(rs.getDouble("remaining_balance"));
        loan.setApplicationDate(rs.getDate("application_date"));
        loan.setApprovalDate(rs.getDate("approval_date"));
        loan.setReleaseDate(rs.getDate("release_date"));
        loan.setMaturityDate(rs.getDate("maturity_date"));
        loan.setLastPaymentDate(rs.getDate("last_payment_date"));
        loan.setStatus(rs.getString("status"));
        loan.setProcessedBy(rs.getInt("processed_by"));
        
        return loan;
    }
    
    /**
     * Creates a new loan application
     * 
     * @param loan Loan object
     * @return New loan ID or -1 if failed
     */
    public static int createLoanApplication(Loan loan) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        int loanId = -1;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Generate loan number
            String loanNumber = generateLoanNumber(loan.getLoanType());
            loan.setLoanNumber(loanNumber);
            
            // Insert loan record
            String query = "INSERT INTO loans (member_id, loan_number, loan_type, principal_amount, " +
                    "interest_rate, term_years, previous_loan_balance, total_deductions, " +
                    "net_loan_proceeds, monthly_amortization, remaining_balance, application_date, " +
                    "status, processed_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, loan.getMemberId());
            stmt.setString(2, loan.getLoanNumber());
            stmt.setString(3, loan.getLoanType());
            stmt.setDouble(4, loan.getPrincipalAmount());
            stmt.setDouble(5, loan.getInterestRate());
            stmt.setInt(6, loan.getTermYears());
            stmt.setDouble(7, loan.getPreviousLoanBalance());
            stmt.setDouble(8, loan.getTotalDeductions());
            stmt.setDouble(9, loan.getNetLoanProceeds());
            stmt.setDouble(10, loan.getMonthlyAmortization());
            stmt.setDouble(11, loan.getPrincipalAmount()); // Initial remaining balance is the principal
            stmt.setDate(12, DateUtils.toSqlDate(DateUtils.getCurrentDate()));
            stmt.setString(13, Constants.LOAN_PENDING); // Initial status is PENDING
            stmt.setInt(14, loan.getProcessedBy());
            
            stmt.executeUpdate();
            generatedKeys = stmt.getGeneratedKeys();
            
            if (generatedKeys.next()) {
                loanId = generatedKeys.getInt(1);
                loan.setId(loanId);
                
                // Insert loan deductions
                saveLoanDeductions(conn, loan);
                
                // Calculate and save amortization schedule
                loan.calculateAmortizationSchedule();
                saveAmortizationSchedule(conn, loan);
                
                conn.commit();
            } else {
                conn.rollback();
                return -1;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return loanId;
    }
    
    /**
     * Approves a loan application
     * 
     * @param loanId Loan ID
     * @param userId User ID who approved the loan
     * @return true if loan approved successfully, false otherwise
     */
    public static boolean approveLoanApplication(int loanId, int userId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get the loan
            Loan loan = getLoanById(loanId);
            if (loan == null || !loan.isPending()) {
                return false;
            }
            
            // Update loan status to APPROVED
            String updateQuery = "UPDATE loans SET status = ?, approval_date = ?, processed_by = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, Constants.LOAN_APPROVED);
                stmt.setDate(2, DateUtils.toSqlDate(DateUtils.getCurrentDate()));
                stmt.setInt(3, userId);
                stmt.setInt(4, loanId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Rejects a loan application
     * 
     * @param loanId Loan ID
     * @param userId User ID who rejected the loan
     * @param reason Reason for rejection
     * @return true if loan rejected successfully, false otherwise
     */
    public static boolean rejectLoanApplication(int loanId, int userId, String reason) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get the loan
            Loan loan = getLoanById(loanId);
            if (loan == null || !loan.isPending()) {
                return false;
            }
            
            // Update loan status to REJECTED
            String updateQuery = "UPDATE loans SET status = ?, processed_by = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, Constants.LOAN_REJECTED);
                stmt.setInt(2, userId);
                stmt.setInt(3, loanId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Releases an approved loan
     * 
     * @param loanId Loan ID
     * @param userId User ID who released the loan
     * @return true if loan released successfully, false otherwise
     */
    public static boolean releaseLoan(int loanId, int userId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get the loan
            Loan loan = getLoanById(loanId);
            if (loan == null || !loan.isApproved()) {
                return false;
            }
            
            // Get member's savings account
            SavingsAccount account = MemberController.getMemberSavingsAccount(loan.getMemberId());
            if (account == null) {
                return false;
            }
            
            // Calculate maturity date
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.getCurrentDate());
            cal.add(Calendar.YEAR, loan.getTermYears());
            java.sql.Date maturityDate = new java.sql.Date(cal.getTimeInMillis());
            
            // Update loan status to ACTIVE and set release and maturity dates
            String updateQuery = "UPDATE loans SET status = ?, release_date = ?, maturity_date = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, Constants.LOAN_ACTIVE);
                stmt.setDate(2, DateUtils.toSqlDate(DateUtils.getCurrentDate()));
                stmt.setDate(3, maturityDate);
                stmt.setInt(4, loanId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Add the net proceeds to the member's savings account
            double newBalance = account.getBalance() + loan.getNetLoanProceeds();
            
            String updateAccountQuery = "UPDATE savings_accounts SET balance = ?, last_activity_date = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateAccountQuery)) {
                stmt.setDouble(1, newBalance);
                stmt.setDate(2, DateUtils.toSqlDate(DateUtils.getCurrentDate()));
                stmt.setInt(3, account.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create transaction record for loan release
            String transactionQuery = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                    "running_balance, reference_number, description, user_id, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                stmt.setInt(1, account.getId());
                stmt.setString(2, Constants.TRANSACTION_LOAN_RELEASE);
                stmt.setDouble(3, loan.getNetLoanProceeds());
                stmt.setDouble(4, newBalance);
                stmt.setString(5, SavingsController.generateReferenceNumber(Constants.TRANSACTION_LOAN_RELEASE));
                stmt.setString(6, "Loan release: " + loan.getLoanNumber());
                stmt.setInt(7, userId);
                stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Update member's last activity date
            MemberController.updateLastActivityDate(loan.getMemberId());
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Records a loan payment
     * 
     * @param loanId Loan ID
     * @param accountId Savings account ID
     * @param amount Payment amount
     * @param description Payment description
     * @param userId User ID who processed the payment
     * @return true if payment recorded successfully, false otherwise
     */
    public static boolean recordLoanPayment(int loanId, int accountId, double amount, String description, int userId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get the loan
            Loan loan = getLoanById(loanId);
            if (loan == null || !loan.isActive()) {
                return false;
            }
            
            // Get the savings account
            SavingsAccount account = SavingsController.getAccountById(accountId);
            if (account == null) {
                return false;
            }
            
            // Check if sufficient balance
            if (account.getBalance() < amount) {
                return false;
            }
            
            // Calculate new remaining balance
            double newRemainingBalance = loan.getRemainingBalance() - amount;
            if (newRemainingBalance < 0) {
                newRemainingBalance = 0;
            }
            
            // Update loan remaining balance
            String updateLoanQuery = "UPDATE loans SET remaining_balance = ?, " +
                    "status = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateLoanQuery)) {
                stmt.setDouble(1, newRemainingBalance);
                
                // If remaining balance is 0, mark as PAID
                String newStatus = newRemainingBalance <= 0 ? Constants.LOAN_PAID : Constants.LOAN_ACTIVE;
                stmt.setString(2, newStatus);
                stmt.setInt(3, loanId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Update savings account balance
            double newAccountBalance = account.getBalance() - amount;
            
            String updateAccountQuery = "UPDATE savings_accounts SET balance = ?, last_activity_date = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateAccountQuery)) {
                stmt.setDouble(1, newAccountBalance);
                stmt.setDate(2, DateUtils.toSqlDate(DateUtils.getCurrentDate()));
                stmt.setInt(3, accountId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create transaction record for loan payment
            String transactionQuery = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                    "running_balance, reference_number, description, user_id, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, Constants.TRANSACTION_LOAN_PAYMENT);
                stmt.setDouble(3, amount);
                stmt.setDouble(4, newAccountBalance);
                stmt.setString(5, SavingsController.generateReferenceNumber(Constants.TRANSACTION_LOAN_PAYMENT));
                stmt.setString(6, description != null && !description.isEmpty() ? 
                        description : "Loan payment for " + loan.getLoanNumber());
                stmt.setInt(7, userId);
                stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Update member's last activity date
            MemberController.updateLastActivityDate(account.getMemberId());
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Gets a loan by ID
     * 
     * @param loanId Loan ID
     * @return Loan object, or null if not found
     */

    
    /**
     * Gets a loan by loan number
     * 
     * @param loanNumber Loan number
     * @return Loan object, or null if not found
     */

    
    /**
     * Gets all loans for a specific member
     * 
     * @param memberId Member ID
     * @return List of loans
     */
    public static List<Loan> getMemberLoans(int memberId) {
        String query = "SELECT * FROM loans WHERE member_id = ? ORDER BY application_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
            
            // Load deductions and amortization schedules for each loan
            for (Loan loan : loans) {
                loadLoanDeductions(loan);
                loadAmortizationSchedule(loan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return loans;
    }
    
    /**
     * Gets all pending loan applications
     * 
     * @return List of pending loans
     */
    public static List<Loan> getPendingLoans() {
        String query = "SELECT * FROM loans WHERE status = ? ORDER BY application_date";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.LOAN_PENDING);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
            
            // Load deductions and amortization schedules for each loan
            for (Loan loan : loans) {
                loadLoanDeductions(loan);
                loadAmortizationSchedule(loan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return loans;
    }
    
    /**
     * Gets all approved but not yet released loans
     * 
     * @return List of approved loans
     */
    public static List<Loan> getApprovedLoans() {
        String query = "SELECT * FROM loans WHERE status = ? ORDER BY approval_date";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.LOAN_APPROVED);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
            
            // Load deductions and amortization schedules for each loan
            for (Loan loan : loans) {
                loadLoanDeductions(loan);
                loadAmortizationSchedule(loan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return loans;
    }
    
    /**
     * Gets all active loans
     * 
     * @return List of active loans
     */
    public static List<Loan> getActiveLoans() {
        String query = "SELECT * FROM loans WHERE status = ? ORDER BY release_date";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.LOAN_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
            
            // Load deductions and amortization schedules for each loan
            for (Loan loan : loans) {
                loadLoanDeductions(loan);
                loadAmortizationSchedule(loan);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return loans;
    }
    
    /**
     * Gets all loan types
     * 
     * @return List of loan types
     */
    public static List<LoanType> getLoanTypes() {
        String query = "SELECT * FROM loan_types ORDER BY name";
        List<LoanType> loanTypes = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LoanType loanType = new LoanType();
                loanType.setId(rs.getInt("id"));
                loanType.setCode(rs.getString("code"));
                loanType.setName(rs.getString("name"));
                loanType.setDescription(rs.getString("description"));
                loanType.setInterestRate(rs.getDouble("interest_rate"));
                loanType.setMinTermMonths(rs.getInt("min_term_months"));
                loanType.setMaxTermMonths(rs.getInt("max_term_months"));
                loanType.setMinAmount(rs.getDouble("min_amount"));
                loanType.setMaxAmount(rs.getDouble("max_amount"));
                loanType.setRequiresRLPF(rs.getBoolean("requires_rlpf"));
                
                loanTypes.add(loanType);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return loanTypes;
    }
    
    /**
     * Gets a loan type by its code
     * 
     * @param code Loan type code
     * @return LoanType object, or null if not found
     */
    public static LoanType getLoanTypeByCode(String code) {
        String query = "SELECT * FROM loan_types WHERE code = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                LoanType loanType = new LoanType();
                loanType.setId(rs.getInt("id"));
                loanType.setCode(rs.getString("code"));
                loanType.setName(rs.getString("name"));
                loanType.setDescription(rs.getString("description"));
                loanType.setInterestRate(rs.getDouble("interest_rate"));
                loanType.setMinTermMonths(rs.getInt("min_term_months"));
                loanType.setMaxTermMonths(rs.getInt("max_term_months"));
                loanType.setMinAmount(rs.getDouble("min_amount"));
                loanType.setMaxAmount(rs.getDouble("max_amount"));
                loanType.setRequiresRLPF(rs.getBoolean("requires_rlpf"));
                
                return loanType;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates or updates a loan type
     * 
     * @param loanType LoanType object
     * @return true if loan type saved successfully, false otherwise
     */
    public static boolean saveLoanType(LoanType loanType) {
        // Check if loan type exists
        LoanType existingType = getLoanTypeByCode(loanType.getCode());
        
        if (existingType == null) {
            // Create new loan type
            String query = "INSERT INTO loan_types (code, name, description, interest_rate, " +
                    "min_term_months, max_term_months, min_amount, max_amount, requires_rlpf) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, loanType.getCode());
                stmt.setString(2, loanType.getName());
                stmt.setString(3, loanType.getDescription());
                stmt.setDouble(4, loanType.getInterestRate());
                stmt.setInt(5, loanType.getMinTermMonths());
                stmt.setInt(6, loanType.getMaxTermMonths());
                stmt.setDouble(7, loanType.getMinAmount());
                stmt.setDouble(8, loanType.getMaxAmount());
                stmt.setBoolean(9, loanType.isRequiresRLPF());
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Update existing loan type
            String query = "UPDATE loan_types SET name = ?, description = ?, interest_rate = ?, " +
                    "min_term_months = ?, max_term_months = ?, min_amount = ?, max_amount = ?, " +
                    "requires_rlpf = ? WHERE code = ?";
            
            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, loanType.getName());
                stmt.setString(2, loanType.getDescription());
                stmt.setDouble(3, loanType.getInterestRate());
                stmt.setInt(4, loanType.getMinTermMonths());
                stmt.setInt(5, loanType.getMaxTermMonths());
                stmt.setDouble(6, loanType.getMinAmount());
                stmt.setDouble(7, loanType.getMaxAmount());
                stmt.setBoolean(8, loanType.isRequiresRLPF());
                stmt.setString(9, loanType.getCode());
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    
    /**
     * Calculates the previous outstanding loan balance for a member
     * 
     * @param memberId Member ID
     * @return Previous loan balance
     */
    public static double calculatePreviousLoanBalance(int memberId) {
        String query = "SELECT SUM(remaining_balance) FROM loans WHERE member_id = ? AND status = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            stmt.setString(2, Constants.LOAN_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Generates a loan number
     * 
     * @param loanType Loan type code
     * @return Generated loan number
     */
    private static String generateLoanNumber(String loanType) {
        String date = DateUtils.getCurrentDate().toString().replaceAll("-", "");
        
        // Get the current highest loan number with today's date
        String prefix = loanType + "-" + date.substring(0, 8) + "-";
        
        String query = "SELECT MAX(loan_number) FROM loans WHERE loan_number LIKE ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            
            int sequence = 1;
            if (rs.next() && rs.getString(1) != null) {
                String lastNumber = rs.getString(1);
                String sequencePart = lastNumber.substring(lastNumber.lastIndexOf("-") + 1);
                sequence = Integer.parseInt(sequencePart) + 1;
            }
            
            return prefix + String.format("%03d", sequence);
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Fallback to a timestamp-based number
            return prefix + System.currentTimeMillis() % 1000;
        }
    }
    
    /**
     * Saves loan deductions to the database
     * 
     * @param conn Database connection
     * @param loan Loan object
     * @throws SQLException If database operation fails
     */
    private static void saveLoanDeductions(Connection conn, Loan loan) throws SQLException {
        String query = "INSERT INTO loan_deductions (loan_id, name, amount, percentage, is_rlpf) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (LoanDeduction deduction : loan.getDeductions()) {
                stmt.setInt(1, loan.getId());
                stmt.setString(2, deduction.getName());
                stmt.setDouble(3, deduction.getAmount());
                stmt.setDouble(4, deduction.getPercentage());
                stmt.setBoolean(5, deduction.isRLPF());
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    /**
     * Saves the amortization schedule to the database
     * 
     * @param conn Database connection
     * @param loan Loan object
     * @throws SQLException If database operation fails
     */
    private static void saveAmortizationSchedule(Connection conn, Loan loan) throws SQLException {
        String query = "INSERT INTO loan_amortization (loan_id, year, remaining_principal, " +
                "annual_interest, annual_principal_payment, monthly_amortization) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (LoanAmortization amortization : loan.getAmortizationSchedule()) {
                stmt.setInt(1, loan.getId());
                stmt.setInt(2, amortization.getYear());
                stmt.setDouble(3, amortization.getRemainingPrincipal());
                stmt.setDouble(4, amortization.getAnnualInterest());
                stmt.setDouble(5, amortization.getAnnualPrincipalPayment());
                stmt.setDouble(6, amortization.getMonthlyAmortization());
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    /**
     * Loads loan deductions from the database
     * 
     * @param loan Loan object
     */
    private static void loadLoanDeductions(Loan loan) {
        String query = "SELECT * FROM loan_deductions WHERE loan_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, loan.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                LoanDeduction deduction = new LoanDeduction();
                deduction.setId(rs.getInt("id"));
                deduction.setLoanId(rs.getInt("loan_id"));
                deduction.setName(rs.getString("name"));
                deduction.setAmount(rs.getDouble("amount"));
                deduction.setPercentage(rs.getDouble("percentage"));
                deduction.setRLPF(rs.getBoolean("is_rlpf"));
                
                loan.addDeduction(deduction);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the amortization schedule from the database
     * 
     * @param loan Loan object
     */
    private static void loadAmortizationSchedule(Loan loan) {
        String query = "SELECT * FROM loan_amortization WHERE loan_id = ? ORDER BY year";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, loan.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                LoanAmortization amortization = new LoanAmortization();
                amortization.setId(rs.getInt("id"));
                amortization.setLoanId(rs.getInt("loan_id"));
                amortization.setYear(rs.getInt("year"));
                amortization.setRemainingPrincipal(rs.getDouble("remaining_principal"));
                amortization.setAnnualInterest(rs.getDouble("annual_interest"));
                amortization.setAnnualPrincipalPayment(rs.getDouble("annual_principal_payment"));
                amortization.setMonthlyAmortization(rs.getDouble("monthly_amortization"));
                
                loan.addAmortization(amortization);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Maps a ResultSet row to a Loan object
     * 
     * @param rs ResultSet
     * @return Loan object
     * @throws SQLException If database operation fails
     */
    private static Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setMemberId(rs.getInt("member_id"));
        loan.setLoanNumber(rs.getString("loan_number"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setPrincipalAmount(rs.getDouble("principal_amount"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setTermYears(rs.getInt("term_years"));
        loan.setPreviousLoanBalance(rs.getDouble("previous_loan_balance"));
        loan.setTotalDeductions(rs.getDouble("total_deductions"));
        loan.setNetLoanProceeds(rs.getDouble("net_loan_proceeds"));
        loan.setMonthlyAmortization(rs.getDouble("monthly_amortization"));
        loan.setRemainingBalance(rs.getDouble("remaining_balance"));
        loan.setApplicationDate(rs.getDate("application_date"));
        loan.setApprovalDate(rs.getDate("approval_date"));
        loan.setReleaseDate(rs.getDate("release_date"));
        loan.setMaturityDate(rs.getDate("maturity_date"));
        loan.setStatus(rs.getString("status"));
        loan.setProcessedBy(rs.getInt("processed_by"));
        
        return loan;
    }
}
