package com.moscat.controllers;

import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for member management
 */
public class MemberController {
    
    /**
     * Creates a new member
     * 
     * @param member Member object
     * @return New member ID or -1 if failed
     */
    public static int createMember(Member member) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        int memberId = -1;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Generate member number (format: MMPC-YYYYMMDD-XXX)
            String memberNumber = generateMemberNumber();
            member.setMemberNumber(memberNumber);
            
            // Insert member
            String query = "INSERT INTO members (member_number, first_name, middle_name, last_name, " +
                    "age, birth_date, present_address, permanent_address, contact_number, email, " +
                    "employer, employment_status, gross_monthly_income, avg_net_monthly_income, " +
                    "status, join_date, last_activity_date, loan_eligibility_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            stmt.setString(paramIndex++, member.getMemberNumber());
            stmt.setString(paramIndex++, member.getFirstName());
            stmt.setString(paramIndex++, member.getMiddleName());
            stmt.setString(paramIndex++, member.getLastName());
            stmt.setInt(paramIndex++, member.getAge());
            stmt.setDate(paramIndex++, member.getBirthDate());
            stmt.setString(paramIndex++, member.getPresentAddress());
            stmt.setString(paramIndex++, member.getPermanentAddress());
            stmt.setString(paramIndex++, member.getContactNumber());
            stmt.setString(paramIndex++, member.getEmail());
            stmt.setString(paramIndex++, member.getEmployer());
            stmt.setString(paramIndex++, member.getEmploymentStatus());
            stmt.setDouble(paramIndex++, member.getGrossMonthlyIncome());
            stmt.setDouble(paramIndex++, member.getAvgNetMonthlyIncome());
            stmt.setString(paramIndex++, Constants.ACCOUNT_ACTIVE); // Default status is ACTIVE
            stmt.setDate(paramIndex++, DateUtils.getCurrentDate()); // Join date is current date
            stmt.setDate(paramIndex++, DateUtils.getCurrentDate()); // Last activity date is current date
            stmt.setDouble(paramIndex++, member.getLoanEligibilityAmount());
            
            stmt.executeUpdate();
            generatedKeys = stmt.getGeneratedKeys();
            
            if (generatedKeys.next()) {
                memberId = generatedKeys.getInt(1);
                
                // Create savings account for the member
                createSavingsAccount(conn, memberId);
                
                // Commit transaction
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
        
        return memberId;
    }
    
    /**
     * Creates a savings account for a member
     * 
     * @param conn Database connection
     * @param memberId Member ID
     * @throws SQLException If database operation fails
     */
    private static void createSavingsAccount(Connection conn, int memberId) throws SQLException {
        String accountNumber = generateAccountNumber(memberId);
        
        String query = "INSERT INTO savings_accounts (member_id, account_number, balance, " +
                "interest_earned, status, open_date, last_activity_date) " +
                "VALUES (?, ?, 0, 0, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setString(2, accountNumber);
            stmt.setString(3, Constants.ACCOUNT_ACTIVE);
            stmt.setDate(4, DateUtils.getCurrentDate());
            stmt.setDate(5, DateUtils.getCurrentDate());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Generates a member number
     * 
     * @return Generated member number
     */
    private static String generateMemberNumber() {
        String date = DateUtils.getCurrentDate().toString().replaceAll("-", "");
        
        // Get the current highest member number with today's date
        String prefix = "MMPC-" + date.substring(0, 8) + "-";
        
        String query = "SELECT MAX(member_number) FROM members WHERE member_number LIKE ?";
        
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
     * Generates an account number for a member
     * 
     * @param memberId Member ID
     * @return Generated account number
     */
    private static String generateAccountNumber(int memberId) {
        // Format: SA-MMPC-XXXXXXXX (where XXXXXXXX is padded member ID)
        return "SA-MMPC-" + String.format("%08d", memberId);
    }
    
    /**
     * Updates an existing member
     * 
     * @param member Member object
     * @return true if member updated successfully, false otherwise
     */
    public static boolean updateMember(Member member) {
        String query = "UPDATE members SET first_name = ?, middle_name = ?, last_name = ?, " +
                "age = ?, birth_date = ?, present_address = ?, permanent_address = ?, " +
                "contact_number = ?, email = ?, employer = ?, employment_status = ?, " +
                "gross_monthly_income = ?, avg_net_monthly_income = ?, status = ?, " +
                "loan_eligibility_amount = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            int paramIndex = 1;
            stmt.setString(paramIndex++, member.getFirstName());
            stmt.setString(paramIndex++, member.getMiddleName());
            stmt.setString(paramIndex++, member.getLastName());
            stmt.setInt(paramIndex++, member.getAge());
            stmt.setDate(paramIndex++, member.getBirthDate());
            stmt.setString(paramIndex++, member.getPresentAddress());
            stmt.setString(paramIndex++, member.getPermanentAddress());
            stmt.setString(paramIndex++, member.getContactNumber());
            stmt.setString(paramIndex++, member.getEmail());
            stmt.setString(paramIndex++, member.getEmployer());
            stmt.setString(paramIndex++, member.getEmploymentStatus());
            stmt.setDouble(paramIndex++, member.getGrossMonthlyIncome());
            stmt.setDouble(paramIndex++, member.getAvgNetMonthlyIncome());
            stmt.setString(paramIndex++, member.getStatus());
            stmt.setDouble(paramIndex++, member.getLoanEligibilityAmount());
            stmt.setInt(paramIndex++, member.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a member (only super admin can delete)
     * 
     * @param memberId Member ID
     * @return true if member deleted successfully, false otherwise
     */
    public static boolean deleteMember(int memberId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // First, delete related records (savings account, transactions, loans)
            deleteRelatedRecords(conn, memberId);
            
            // Then delete the member
            String query = "DELETE FROM members WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
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
     * Deletes all records related to a member
     * 
     * @param conn Database connection
     * @param memberId Member ID
     * @throws SQLException If database operation fails
     */
    private static void deleteRelatedRecords(Connection conn, int memberId) throws SQLException {
        // First, get the savings account ID
        int accountId = -1;
        String accountQuery = "SELECT id FROM savings_accounts WHERE member_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(accountQuery)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt("id");
            }
        }
        
        // Delete transactions
        if (accountId != -1) {
            String transactionQuery = "DELETE FROM transactions WHERE account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                stmt.setInt(1, accountId);
                stmt.executeUpdate();
            }
        }
        
        // Delete loan amortization schedules
        String amortizationQuery = "DELETE FROM loan_amortization WHERE loan_id IN " +
                "(SELECT id FROM loans WHERE member_id = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(amortizationQuery)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
        
        // Delete loan deductions
        String deductionQuery = "DELETE FROM loan_deductions WHERE loan_id IN " +
                "(SELECT id FROM loans WHERE member_id = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(deductionQuery)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
        
        // Delete loans
        String loanQuery = "DELETE FROM loans WHERE member_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(loanQuery)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
        
        // Delete savings account
        String accountDelete = "DELETE FROM savings_accounts WHERE member_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(accountDelete)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Gets a member by ID
     * 
     * @param memberId Member ID
     * @return Member object, or null if not found
     */
    public static Member getMemberById(int memberId) {
        String query = "SELECT * FROM members WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets a member by member number
     * 
     * @param memberNumber Member number
     * @return Member object, or null if not found
     */
    public static Member getMemberByNumber(String memberNumber) {
        String query = "SELECT * FROM members WHERE member_number = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, memberNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all members
     * 
     * @return List of all members
     */
    public static List<Member> getAllMembers() {
        String query = "SELECT * FROM members ORDER BY last_name, first_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Gets all active members
     * 
     * @return List of active members
     */
    public static List<Member> getActiveMembers() {
        String query = "SELECT * FROM members WHERE status = ? ORDER BY last_name, first_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.ACCOUNT_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Gets dormant member accounts (no activity for 12 months)
     * 
     * @return List of dormant members
     */
    public static List<Member> getDormantMembers() {
        String query = "SELECT * FROM members WHERE status = ? ORDER BY last_name, first_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.ACCOUNT_DORMANT);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Searches for members matching the given criteria
     * 
     * @param searchText Text to search for in names, member number, contact, or email
     * @return List of matching members
     */
    public static List<Member> searchMembers(String searchText) {
        String query = "SELECT * FROM members WHERE " +
                "LOWER(first_name) LIKE ? OR " +
                "LOWER(middle_name) LIKE ? OR " +
                "LOWER(last_name) LIKE ? OR " +
                "LOWER(member_number) LIKE ? OR " +
                "LOWER(contact_number) LIKE ? OR " +
                "LOWER(email) LIKE ? " +
                "ORDER BY last_name, first_name";
        
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchPattern);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Updates the status of a member account
     * 
     * @param memberId Member ID
     * @param status New status
     * @return true if status updated successfully, false otherwise
     */
    public static boolean updateMemberStatus(int memberId, String status) {
        String query = "UPDATE members SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, memberId);
            
            int rowsAffected = stmt.executeUpdate();
            
            // Also update the savings account status
            if (rowsAffected > 0) {
                updateSavingsAccountStatus(memberId, status);
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates the status of a member's savings account
     * 
     * @param memberId Member ID
     * @param status New status
     * @throws SQLException If database operation fails
     */
    private static void updateSavingsAccountStatus(int memberId, String status) throws SQLException {
        String query = "UPDATE savings_accounts SET status = ? WHERE member_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, memberId);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to a Member object
     * 
     * @param rs ResultSet
     * @return Member object
     * @throws SQLException If database operation fails
     */
    private static Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setMemberNumber(rs.getString("member_number"));
        member.setFirstName(rs.getString("first_name"));
        member.setMiddleName(rs.getString("middle_name"));
        member.setLastName(rs.getString("last_name"));
        member.setAge(rs.getInt("age"));
        member.setBirthDate(rs.getDate("birth_date"));
        member.setPresentAddress(rs.getString("present_address"));
        member.setPermanentAddress(rs.getString("permanent_address"));
        member.setContactNumber(rs.getString("contact_number"));
        member.setEmail(rs.getString("email"));
        member.setEmployer(rs.getString("employer"));
        member.setEmploymentStatus(rs.getString("employment_status"));
        member.setGrossMonthlyIncome(rs.getDouble("gross_monthly_income"));
        member.setAvgNetMonthlyIncome(rs.getDouble("avg_net_monthly_income"));
        member.setStatus(rs.getString("status"));
        member.setJoinDate(rs.getDate("join_date"));
        member.setLastActivityDate(rs.getDate("last_activity_date"));
        member.setLoanEligibilityAmount(rs.getDouble("loan_eligibility_amount"));
        
        return member;
    }
    
    /**
     * Gets a member's savings account
     * 
     * @param memberId Member ID
     * @return SavingsAccount object, or null if not found
     */
    public static SavingsAccount getMemberSavingsAccount(int memberId) {
        String query = "SELECT * FROM savings_accounts WHERE member_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                SavingsAccount account = new SavingsAccount();
                account.setId(rs.getInt("id"));
                account.setMemberId(rs.getInt("member_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setBalance(rs.getDouble("balance"));
                account.setInterestEarned(rs.getDouble("interest_earned"));
                account.setLastInterestComputationDate(rs.getDate("last_interest_computation_date"));
                account.setStatus(rs.getString("status"));
                account.setOpenDate(rs.getDate("open_date"));
                account.setLastActivityDate(rs.getDate("last_activity_date"));
                
                return account;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Updates a member's loan eligibility amount
     * 
     * @param memberId Member ID
     * @param amount New loan eligibility amount
     * @return true if updated successfully, false otherwise
     */
    public static boolean updateLoanEligibility(int memberId, double amount) {
        String query = "UPDATE members SET loan_eligibility_amount = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, amount);
            stmt.setInt(2, memberId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates the last activity date for a member
     * 
     * @param memberId Member ID
     * @return true if updated successfully, false otherwise
     */
    public static boolean updateLastActivityDate(int memberId) {
        String query = "UPDATE members SET last_activity_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, DateUtils.getCurrentDate());
            stmt.setInt(2, memberId);
            
            int rowsAffected = stmt.executeUpdate();
            
            // Also update the savings account last activity date
            if (rowsAffected > 0) {
                updateSavingsAccountLastActivityDate(memberId);
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates the last activity date for a member's savings account
     * 
     * @param memberId Member ID
     * @throws SQLException If database operation fails
     */
    private static void updateSavingsAccountLastActivityDate(int memberId) throws SQLException {
        String query = "UPDATE savings_accounts SET last_activity_date = ? WHERE member_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, DateUtils.getCurrentDate());
            stmt.setInt(2, memberId);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Checks for dormant accounts and updates their status
     * 
     * @return Number of accounts updated to dormant
     */
    public static int checkAndUpdateDormantAccounts() {
        String query = "SELECT id FROM members WHERE status = ? AND " +
                "DATEDIFF('MONTH', last_activity_date, CURRENT_DATE) >= 12";
        
        List<Integer> dormantMemberIds = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.ACCOUNT_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                dormantMemberIds.add(rs.getInt("id"));
            }
            
            // Update status to dormant for each identified member
            for (int memberId : dormantMemberIds) {
                updateMemberStatus(memberId, Constants.ACCOUNT_DORMANT);
            }
            
            return dormantMemberIds.size();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
