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

import com.moscat.models.Member;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

/**
 * Controller for member-related operations
 */
public class MemberController {
    
    /**
     * Creates a new member
     * 
     * @param member The member to create
     * @return True if successful, false otherwise
     */
    public static boolean createMember(Member member) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "INSERT INTO members "
                    + "(first_name, middle_name, last_name, age, birthdate, present_address, permanent_address, "
                    + "contact_number, email_address, employer, employment_status, gross_monthly_income, average_net_monthly_income, "
                    + "created_at, updated_at, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, member.getFirstName());
                stmt.setString(2, member.getMiddleName());
                stmt.setString(3, member.getLastName());
                stmt.setInt(4, member.getAge());
                stmt.setDate(5, java.sql.Date.valueOf(member.getBirthdate()));
                stmt.setString(6, member.getPresentAddress());
                stmt.setString(7, member.getPermanentAddress());
                stmt.setString(8, member.getContactNumber());
                stmt.setString(9, member.getEmailAddress());
                stmt.setString(10, member.getEmployer());
                stmt.setString(11, member.getEmploymentStatus());
                stmt.setDouble(12, member.getGrossMonthlyIncome());
                stmt.setDouble(13, member.getAverageNetMonthlyIncome());
                
                LocalDateTime now = LocalDateTime.now();
                stmt.setTimestamp(14, Timestamp.valueOf(now));
                stmt.setTimestamp(15, Timestamp.valueOf(now));
                stmt.setString(16, Constants.STATUS_ACTIVE);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error creating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing member
     * 
     * @param member The member to update
     * @return True if successful, false otherwise
     */
    public static boolean updateMember(Member member) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET "
                    + "first_name = ?, middle_name = ?, last_name = ?, age = ?, birthdate = ?, "
                    + "present_address = ?, permanent_address = ?, contact_number = ?, email_address = ?, "
                    + "employer = ?, employment_status = ?, gross_monthly_income = ?, average_net_monthly_income = ?, "
                    + "updated_at = ?, status = ? "
                    + "WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, member.getFirstName());
                stmt.setString(2, member.getMiddleName());
                stmt.setString(3, member.getLastName());
                stmt.setInt(4, member.getAge());
                stmt.setDate(5, java.sql.Date.valueOf(member.getBirthdate()));
                stmt.setString(6, member.getPresentAddress());
                stmt.setString(7, member.getPermanentAddress());
                stmt.setString(8, member.getContactNumber());
                stmt.setString(9, member.getEmailAddress());
                stmt.setString(10, member.getEmployer());
                stmt.setString(11, member.getEmploymentStatus());
                stmt.setDouble(12, member.getGrossMonthlyIncome());
                stmt.setDouble(13, member.getAverageNetMonthlyIncome());
                stmt.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(15, member.getStatus());
                stmt.setInt(16, member.getId());
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets a member by ID
     * 
     * @param memberId The member ID
     * @return The member, or null if not found
     */
    public static Member getMemberById(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return extractMemberFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member: " + e.getMessage());
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
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members ORDER BY last_name, first_name";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    members.add(extractMemberFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Gets dormant members
     * 
     * @return List of dormant members
     */
    public static List<Member> getDormantMembers() {
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE status = ? ORDER BY last_name, first_name";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_DORMANT);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        members.add(extractMemberFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting dormant members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Updates a member's savings balance
     * 
     * @param memberId The member ID
     * @param newBalance The new balance
     * @return True if successful, false otherwise
     */
    public static boolean updateSavingsBalance(int memberId, double newBalance) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET savings_balance = ?, updated_at = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDouble(1, newBalance);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(3, memberId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating savings balance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates a member's interest earned
     * 
     * @param memberId The member ID
     * @param additionalInterest The additional interest to add
     * @return True if successful, false otherwise
     */
    public static boolean addInterestEarned(int memberId, double additionalInterest) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET interest_earned = interest_earned + ?, updated_at = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDouble(1, additionalInterest);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(3, memberId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating interest earned: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the total number of members
     * 
     * @return The member count
     */
    public static int getMemberCount() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM members";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Gets the total savings balance of all members
     * 
     * @return The total savings balance
     */
    public static double getTotalSavingsBalance() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT SUM(savings_balance) FROM members";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total savings balance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Sets a member's account as dormant
     * 
     * @param memberId The member ID
     * @return True if successful, false otherwise
     */
    public static boolean setMemberDormant(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET status = ?, updated_at = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_DORMANT);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(3, memberId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error setting member dormant: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reactivates a dormant member account
     * 
     * @param memberId The member ID
     * @return True if successful, false otherwise
     */
    public static boolean reactivateMember(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET status = ?, updated_at = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_ACTIVE);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(3, memberId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error reactivating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extracts a Member object from a ResultSet
     * 
     * @param rs The ResultSet
     * @return The extracted Member
     * @throws SQLException If a database error occurs
     */
    private static Member extractMemberFromResultSet(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setMiddleName(rs.getString("middle_name"));
        member.setLastName(rs.getString("last_name"));
        member.setAge(rs.getInt("age"));
        member.setBirthdate(rs.getDate("birthdate").toLocalDate());
        member.setPresentAddress(rs.getString("present_address"));
        member.setPermanentAddress(rs.getString("permanent_address"));
        member.setContactNumber(rs.getString("contact_number"));
        member.setEmailAddress(rs.getString("email_address"));
        member.setEmployer(rs.getString("employer"));
        member.setEmploymentStatus(rs.getString("employment_status"));
        member.setGrossMonthlyIncome(rs.getDouble("gross_monthly_income"));
        member.setAverageNetMonthlyIncome(rs.getDouble("average_net_monthly_income"));
        member.setSavingsBalance(rs.getDouble("savings_balance"));
        member.setInterestEarned(rs.getDouble("interest_earned"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            member.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            member.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        member.setStatus(rs.getString("status"));
        
        return member;
    }
}