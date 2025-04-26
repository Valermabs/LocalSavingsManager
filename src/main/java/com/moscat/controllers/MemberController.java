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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Controller for member management
 */
public class MemberController {
    
    /**
     * Gets a member by ID
     * 
     * @param memberId Member ID
     * @return Member or null if not found
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
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets a member by member number
     * 
     * @param memberNumber Member number
     * @return Member or null if not found
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
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
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
     * Gets active members
     * 
     * @return List of active members
     */
    public static List<Member> getActiveMembers() {
        String query = "SELECT * FROM members WHERE status = ? ORDER BY last_name, first_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.STATUS_ACTIVE);
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
     * Searches for members
     * 
     * @param searchTerm Search term
     * @return List of matching members
     */
    public static List<Member> searchMembers(String searchTerm) {
        String query = "SELECT * FROM members WHERE member_number LIKE ? OR first_name LIKE ? OR " +
                "middle_name LIKE ? OR last_name LIKE ? OR contact_number LIKE ? OR email LIKE ? " +
                "ORDER BY last_name, first_name";
        
        List<Member> members = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            stmt.setString(6, searchPattern);
            
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
     * Registers a new member
     * 
     * @param member Member object
     * @return true if registration successful, false otherwise
     */
    public static boolean registerMember(Member member) {
        if (member.getFirstName() == null || member.getLastName() == null) {
            return false;
        }
        
        // Generate member number
        String memberNumber = generateMemberNumber();
        member.setMemberNumber(memberNumber);
        
        // Set status and join date if not set
        if (member.getStatus() == null) {
            member.setStatus(Constants.STATUS_ACTIVE);
        }
        if (member.getJoinDate() == null) {
            member.setJoinDate(new Date());
        }
        
        String query = "INSERT INTO members (member_number, first_name, middle_name, last_name, " +
                "birth_date, contact_number, email, present_address, permanent_address, " +
                "employer, employment_status, gross_monthly_income, avg_net_monthly_income, " +
                "status, join_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, member.getMemberNumber());
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getMiddleName());
            stmt.setString(4, member.getLastName());
            stmt.setDate(5, member.getBirthDate() != null ? DateUtils.toSqlDate(member.getBirthDate()) : null);
            stmt.setString(6, member.getContactNumber());
            stmt.setString(7, member.getEmail());
            stmt.setString(8, member.getPresentAddress());
            stmt.setString(9, member.getPermanentAddress());
            stmt.setString(10, member.getEmployer());
            stmt.setString(11, member.getEmploymentStatus());
            stmt.setDouble(12, member.getGrossMonthlyIncome());
            stmt.setDouble(13, member.getAvgNetMonthlyIncome());
            stmt.setString(14, member.getStatus());
            stmt.setDate(15, member.getJoinDate() != null ? DateUtils.toSqlDate(member.getJoinDate()) : null);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates a member
     * 
     * @param member Member object
     * @return true if update successful, false otherwise
     */
    public static boolean updateMember(Member member) {
        if (member.getId() <= 0 || member.getMemberNumber() == null) {
            return false;
        }
        
        String query = "UPDATE members SET first_name = ?, middle_name = ?, last_name = ?, " +
                "birth_date = ?, contact_number = ?, email = ?, present_address = ?, " +
                "permanent_address = ?, employer = ?, employment_status = ?, " +
                "gross_monthly_income = ?, avg_net_monthly_income = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getMiddleName());
            stmt.setString(3, member.getLastName());
            stmt.setDate(4, member.getBirthDate() != null ? DateUtils.toSqlDate(member.getBirthDate()) : null);
            stmt.setString(5, member.getContactNumber());
            stmt.setString(6, member.getEmail());
            stmt.setString(7, member.getPresentAddress());
            stmt.setString(8, member.getPermanentAddress());
            stmt.setString(9, member.getEmployer());
            stmt.setString(10, member.getEmploymentStatus());
            stmt.setDouble(11, member.getGrossMonthlyIncome());
            stmt.setDouble(12, member.getAvgNetMonthlyIncome());
            stmt.setInt(13, member.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Activates a member
     * 
     * @param memberNumber Member number
     * @return true if activation successful, false otherwise
     */
    public static boolean activateMember(String memberNumber) {
        return updateMemberStatus(memberNumber, Constants.STATUS_ACTIVE);
    }
    
    /**
     * Deactivates a member
     * 
     * @param memberNumber Member number
     * @return true if deactivation successful, false otherwise
     */
    public static boolean deactivateMember(String memberNumber) {
        return updateMemberStatus(memberNumber, Constants.STATUS_INACTIVE);
    }
    
    /**
     * Updates a member's status
     * 
     * @param memberNumber Member number
     * @param status New status
     * @return true if update successful, false otherwise
     */
    private static boolean updateMemberStatus(String memberNumber, String status) {
        String query = "UPDATE members SET status = ? WHERE member_number = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setString(2, memberNumber);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets savings accounts for a member
     * 
     * @param memberId Member ID
     * @return List of savings accounts
     */
    public static List<SavingsAccount> getMemberSavingsAccounts(int memberId) {
        String query = "SELECT * FROM savings_accounts WHERE member_id = ? ORDER BY open_date DESC";
        List<SavingsAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SavingsAccount account = new SavingsAccount();
                account.setId(rs.getInt("id"));
                account.setMemberId(rs.getInt("member_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setBalance(rs.getDouble("balance"));
                account.setInterestEarned(rs.getDouble("interest_earned"));
                account.setStatus(rs.getString("status"));
                account.setOpenDate(rs.getDate("open_date"));
                account.setLastActivityDate(rs.getDate("last_activity_date"));
                accounts.add(account);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    /**
     * Maps a ResultSet row to a Member object
     * 
     * @param rs ResultSet
     * @return Member object
     * @throws SQLException If database error occurs
     */
    private static Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setMemberNumber(rs.getString("member_number"));
        member.setFirstName(rs.getString("first_name"));
        member.setMiddleName(rs.getString("middle_name"));
        member.setLastName(rs.getString("last_name"));
        member.setBirthDate(rs.getDate("birth_date"));
        member.setContactNumber(rs.getString("contact_number"));
        member.setEmail(rs.getString("email"));
        member.setPresentAddress(rs.getString("present_address"));
        member.setPermanentAddress(rs.getString("permanent_address"));
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
     * Generates a unique member number
     * 
     * @return Generated member number
     */
    private static String generateMemberNumber() {
        // Format: MM-YYYYMMDD-XXXX (where XXXX is a random 4-digit number)
        String prefix = "MM-";
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        
        return prefix + datePart + "-" + randomPart;
    }
}