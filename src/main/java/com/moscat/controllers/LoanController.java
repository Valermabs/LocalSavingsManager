package com.moscat.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moscat.models.Loan;
import com.moscat.models.LoanAmortization;
import com.moscat.models.Member;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

/**
 * Controller for loan-related operations
 */
public class LoanController {
    
    /**
     * Creates a new loan
     * 
     * @param loan The loan to create
     * @param processedBy The username of the user who processed the loan
     * @return True if successful, false otherwise
     */
    public static boolean createLoan(Loan loan, String processedBy) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Insert loan record
                String insertLoanQuery = "INSERT INTO loans "
                        + "(member_id, loan_type, loan_amount, interest_rate, previous_loan_balance, "
                        + "deductions, rlpf, net_proceeds, term_months, application_date, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                int loanId;
                
                try (PreparedStatement stmt = conn.prepareStatement(insertLoanQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, loan.getMemberId());
                    stmt.setString(2, loan.getLoanType());
                    stmt.setDouble(3, loan.getLoanAmount());
                    stmt.setDouble(4, loan.getInterestRate());
                    stmt.setDouble(5, loan.getPreviousLoanBalance());
                    stmt.setDouble(6, loan.getDeductions());
                    stmt.setDouble(7, loan.getRlpf());
                    stmt.setDouble(8, loan.getNetProceeds());
                    stmt.setInt(9, loan.getTermMonths());
                    stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setString(11, Constants.LOAN_STATUS_PENDING);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                    
                    // Get the generated loan ID
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            loanId = generatedKeys.getInt(1);
                            loan.setId(loanId);
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                
                // Generate amortization schedule
                loan.generateAmortizationSchedule();
                
                // Insert amortization schedule records
                String insertAmortizationQuery = "INSERT INTO loan_amortization "
                        + "(loan_id, payment_number, payment_date, principal_amount, interest_amount, "
                        + "total_payment, remaining_balance, payment_status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(insertAmortizationQuery)) {
                    for (LoanAmortization amortization : loan.getAmortizationSchedule()) {
                        stmt.setInt(1, loanId);
                        stmt.setInt(2, amortization.getPaymentNumber());
                        stmt.setDate(3, java.sql.Date.valueOf(amortization.getPaymentDate()));
                        stmt.setDouble(4, amortization.getPrincipalAmount());
                        stmt.setDouble(5, amortization.getInterestAmount());
                        stmt.setDouble(6, amortization.getTotalPayment());
                        stmt.setDouble(7, amortization.getRemainingBalance());
                        stmt.setString(8, Constants.PAYMENT_STATUS_UNPAID);
                        
                        stmt.addBatch();
                    }
                    
                    int[] batchResults = stmt.executeBatch();
                    for (int result : batchResults) {
                        if (result <= 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                
                // Commit the transaction
                conn.commit();
                return true;
            } catch (Exception e) {
                // Rollback in case of an error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error creating loan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Approves a loan
     * 
     * @param loanId The loan ID
     * @param processedBy The username of the user who approved the loan
     * @return True if successful, false otherwise
     */
    public static boolean approveLoan(int loanId, String processedBy) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE loans SET status = ?, approval_date = ? WHERE id = ? AND status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.LOAN_STATUS_APPROVED);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(3, loanId);
                stmt.setString(4, Constants.LOAN_STATUS_PENDING);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error approving loan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Rejects a loan
     * 
     * @param loanId The loan ID
     * @param processedBy The username of the user who rejected the loan
     * @return True if successful, false otherwise
     */
    public static boolean rejectLoan(int loanId, String processedBy) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE loans SET status = ? WHERE id = ? AND status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.LOAN_STATUS_REJECTED);
                stmt.setInt(2, loanId);
                stmt.setString(3, Constants.LOAN_STATUS_PENDING);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error rejecting loan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Releases a loan (disburses funds)
     * 
     * @param loanId The loan ID
     * @param processedBy The username of the user who released the loan
     * @return True if successful, false otherwise
     */
    public static boolean releaseLoan(int loanId, String processedBy) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Get the loan
                Loan loan = getLoanById(loanId);
                if (loan == null || !Constants.LOAN_STATUS_APPROVED.equals(loan.getStatus())) {
                    return false;
                }
                
                // Get the member
                Member member = MemberController.getMemberById(loan.getMemberId());
                if (member == null) {
                    return false;
                }
                
                // Update loan status to active
                String updateLoanQuery = "UPDATE loans SET status = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateLoanQuery)) {
                    stmt.setString(1, Constants.LOAN_STATUS_ACTIVE);
                    stmt.setInt(2, loanId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Add loan release transaction
                double netProceeds = loan.getNetProceeds();
                String description = String.format("Loan Release: %s (ID: %d)", loan.getLoanType(), loanId);
                
                if (!TransactionController.processDeposit(loan.getMemberId(), netProceeds, description, processedBy)) {
                    conn.rollback();
                    return false;
                }
                
                // Commit the transaction
                conn.commit();
                return true;
            } catch (Exception e) {
                // Rollback in case of an error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error releasing loan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Records a loan payment
     * 
     * @param loanId The loan ID
     * @param amortizationId The amortization ID
     * @param amount The payment amount
     * @param processedBy The username of the user who processed the payment
     * @return True if successful, false otherwise
     */
    public static boolean recordLoanPayment(int loanId, int amortizationId, double amount, String processedBy) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Get the loan
                Loan loan = getLoanById(loanId);
                if (loan == null || !isActive(loan)) {
                    return false;
                }
                
                // Get the member
                Member member = MemberController.getMemberById(loan.getMemberId());
                if (member == null) {
                    return false;
                }
                
                // Get the amortization record
                LoanAmortization amortization = getAmortizationById(amortizationId);
                if (amortization == null || Constants.PAYMENT_STATUS_PAID.equals(amortization.getPaymentStatus())) {
                    return false;
                }
                
                // Check if amount is sufficient
                if (amount < amortization.getTotalPayment()) {
                    // Partial payment
                    // TODO: Implement partial payment logic if needed
                    conn.rollback();
                    return false;
                }
                
                // Update amortization record
                String updateAmortizationQuery = "UPDATE loan_amortization SET payment_status = ?, actual_payment_date = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateAmortizationQuery)) {
                    stmt.setString(1, Constants.PAYMENT_STATUS_PAID);
                    stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                    stmt.setInt(3, amortizationId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Record withdrawal transaction
                String description = String.format("Loan Payment: %s (ID: %d) - Payment #%d", 
                        loan.getLoanType(), loanId, amortization.getPaymentNumber());
                
                if (!TransactionController.processWithdrawal(loan.getMemberId(), amount, description, processedBy)) {
                    conn.rollback();
                    return false;
                }
                
                // Check if loan is fully paid
                boolean isFullyPaid = isLoanFullyPaid(loanId);
                if (isFullyPaid) {
                    // Update loan status to paid
                    String updateLoanQuery = "UPDATE loans SET status = ? WHERE id = ? AND status = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateLoanQuery)) {
                        stmt.setString(1, Constants.LOAN_PAID);
                        stmt.setInt(2, loanId);
                        stmt.setString(3, Constants.LOAN_ACTIVE);
                        
                        stmt.executeUpdate();
                    }
                }
                
                // Commit the transaction
                conn.commit();
                return true;
            } catch (Exception e) {
                // Rollback in case of an error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error recording loan payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the amount due for a loan
     * 
     * @param loanId The loan ID
     * @return The total amount due
     */
    public static double getLoanAmountDue(int loanId) {
        double amountDue = 0.0;
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT SUM(total_payment) FROM loan_amortization WHERE loan_id = ? AND payment_status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, loanId);
                stmt.setString(2, Constants.PAYMENT_STATUS_UNPAID);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        amountDue = rs.getDouble(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting loan amount due: " + e.getMessage());
            e.printStackTrace();
        }
        
        return amountDue;
    }
    
    /**
     * Gets the remaining balance for a loan
     * 
     * @param loanId The loan ID
     * @return The remaining balance
     */
    public static double getRemainingBalance(int loanId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT MAX(remaining_balance) FROM loan_amortization "
                    + "WHERE loan_id = ? AND payment_status = ? ORDER BY payment_number DESC LIMIT 1";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, loanId);
                stmt.setString(2, Constants.PAYMENT_STATUS_UNPAID);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            }
            
            // If no unpaid amortizations, check the loan amount
            Loan loan = getLoanById(loanId);
            if (loan != null) {
                return loan.getLoanAmount();
            }
        } catch (SQLException e) {
            System.err.println("Error getting remaining balance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Checks if a loan is fully paid
     * 
     * @param loanId The loan ID
     * @return True if fully paid, false otherwise
     */
    public static boolean isLoanFullyPaid(int loanId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM loan_amortization WHERE loan_id = ? AND payment_status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, loanId);
                stmt.setString(2, Constants.PAYMENT_STATUS_UNPAID);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) == 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if loan is fully paid: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets a loan by ID
     * 
     * @param loanId The loan ID
     * @return The loan, or null if not found
     */
    public static Loan getLoanById(int loanId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM loans WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, loanId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Loan loan = extractLoanFromResultSet(rs);
                        
                        // Get amortization schedule
                        loan.setAmortizationSchedule(getAmortizationSchedule(loanId));
                        
                        return loan;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting loan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets loans for a member
     * 
     * @param memberId The member ID
     * @return List of loans for the member
     */
    public static List<Loan> getMemberLoans(int memberId) {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM loans WHERE member_id = ? ORDER BY application_date DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Loan loan = extractLoanFromResultSet(rs);
                        loans.add(loan);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member loans: " + e.getMessage());
            e.printStackTrace();
        }
        
        return loans;
    }
    
    /**
     * Gets active loans for a member
     * 
     * @param memberId The member ID
     * @return List of active loans for the member
     */
    public static List<Loan> getActiveMemberLoans(int memberId) {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM loans WHERE member_id = ? AND status = ? ORDER BY application_date DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                stmt.setString(2, Constants.LOAN_STATUS_ACTIVE);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Loan loan = extractLoanFromResultSet(rs);
                        loans.add(loan);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active member loans: " + e.getMessage());
            e.printStackTrace();
        }
        
        return loans;
    }
    
    /**
     * Gets the amortization schedule for a loan
     * 
     * @param loanId The loan ID
     * @return List of amortization records
     */
    public static List<LoanAmortization> getAmortizationSchedule(int loanId) {
        List<LoanAmortization> schedule = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM loan_amortization WHERE loan_id = ? ORDER BY payment_number";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, loanId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        schedule.add(extractAmortizationFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting amortization schedule: " + e.getMessage());
            e.printStackTrace();
        }
        
        return schedule;
    }
    
    /**
     * Gets an amortization record by ID
     * 
     * @param amortizationId The amortization ID
     * @return The amortization record, or null if not found
     */
    public static LoanAmortization getAmortizationById(int amortizationId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM loan_amortization WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, amortizationId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return extractAmortizationFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting amortization: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets overdue loan payments
     * 
     * @return List of overdue amortization records
     */
    public static List<LoanAmortization> getOverduePayments() {
        List<LoanAmortization> overduePayments = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT la.* FROM loan_amortization la "
                    + "JOIN loans l ON la.loan_id = l.id "
                    + "WHERE la.payment_date < ? AND la.payment_status = ? AND l.status = ? "
                    + "ORDER BY la.payment_date";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                stmt.setString(2, Constants.PAYMENT_STATUS_UNPAID);
                stmt.setString(3, Constants.LOAN_STATUS_ACTIVE);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        overduePayments.add(extractAmortizationFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue payments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return overduePayments;
    }
    
    /**
     * Extracts a Loan object from a ResultSet
     * 
     * @param rs The ResultSet
     * @return The extracted Loan
     * @throws SQLException If a database error occurs
     */
    private static Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setMemberId(rs.getInt("member_id"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setLoanAmount(rs.getDouble("loan_amount"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setPreviousLoanBalance(rs.getDouble("previous_loan_balance"));
        loan.setDeductions(rs.getDouble("deductions"));
        loan.setRlpf(rs.getDouble("rlpf"));
        loan.setNetProceeds(rs.getDouble("net_proceeds"));
        loan.setTermMonths(rs.getInt("term_months"));
        
        Timestamp applicationDate = rs.getTimestamp("application_date");
        if (applicationDate != null) {
            loan.setApplicationDate(applicationDate.toLocalDateTime());
        }
        
        Timestamp approvalDate = rs.getTimestamp("approval_date");
        if (approvalDate != null) {
            loan.setApprovalDate(approvalDate.toLocalDateTime());
        }
        
        loan.setStatus(rs.getString("status"));
        
        return loan;
    }
    
    /**
     * Extracts a LoanAmortization object from a ResultSet
     * 
     * @param rs The ResultSet
     * @return The extracted LoanAmortization
     * @throws SQLException If a database error occurs
     */
    private static LoanAmortization extractAmortizationFromResultSet(ResultSet rs) throws SQLException {
        LoanAmortization amortization = new LoanAmortization();
        amortization.setId(rs.getInt("id"));
        amortization.setLoanId(rs.getInt("loan_id"));
        amortization.setPaymentNumber(rs.getInt("payment_number"));
        
        java.sql.Date paymentDate = rs.getDate("payment_date");
        if (paymentDate != null) {
            amortization.setPaymentDate(paymentDate.toLocalDate());
        }
        
        amortization.setPrincipalAmount(rs.getDouble("principal_amount"));
        amortization.setInterestAmount(rs.getDouble("interest_amount"));
        amortization.setTotalPayment(rs.getDouble("total_payment"));
        amortization.setRemainingBalance(rs.getDouble("remaining_balance"));
        amortization.setPaymentStatus(rs.getString("payment_status"));
        
        java.sql.Date actualPaymentDate = rs.getDate("actual_payment_date");
        if (actualPaymentDate != null) {
            amortization.setActualPaymentDate(actualPaymentDate.toLocalDate());
        }
        
        return amortization;
    }
    
    /**
     * Checks if a loan is active
     * 
     * @param loan The loan to check
     * @return True if active, false otherwise
     */
    public static boolean isActive(Loan loan) {
        return loan != null && Constants.LOAN_STATUS_ACTIVE.equals(loan.getStatus());
    }
    
    /**
     * Gets the remaining balance for a loan
     * 
     * @param loan The loan
     * @return The remaining balance
     */
    public static double getRemainingBalance(Loan loan) {
        if (loan == null) {
            return 0.0;
        }
        return getRemainingBalance(loan.getId());
    }
    
    /**
     * Gets the loan number (for display purposes)
     * 
     * @param loan The loan
     * @return The loan number string
     */
    public static String getLoanNumber(Loan loan) {
        if (loan == null) {
            return "N/A";
        }
        
        return String.format("LN-%06d", loan.getId());
    }
    
    /**
     * Updates the last activity date for a member
     * 
     * @param memberId The member ID
     * @return True if successful, false otherwise
     */
    public static boolean updateLastActivityDate(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET updated_at = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(2, memberId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating last activity date: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}