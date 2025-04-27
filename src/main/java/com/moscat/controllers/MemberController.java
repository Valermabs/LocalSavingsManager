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
            // Generate a unique member number if not provided
            if (member.getMemberNumber() == null || member.getMemberNumber().isEmpty()) {
                member.setMemberNumber(generateMemberNumber());
            }
            
            // Set join date to current date if not provided
            if (member.getJoinDate() == null) {
                member.setJoinDate(new java.util.Date());
            }
            
            // Set last activity date to join date if not provided
            if (member.getLastActivityDate() == null) {
                member.setLastActivityDate(member.getJoinDate());
            }
            
            String query = "INSERT INTO members "
                    + "(member_number, first_name, middle_name, last_name, age, birthdate, present_address, permanent_address, "
                    + "contact_number, email_address, employer, employment_status, gross_monthly_income, average_net_monthly_income, "
                    + "join_date, last_activity_date, created_at, updated_at, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, member.getMemberNumber());
                stmt.setString(2, member.getFirstName());
                stmt.setString(3, member.getMiddleName());
                stmt.setString(4, member.getLastName());
                stmt.setInt(5, member.getAge());
                stmt.setDate(6, java.sql.Date.valueOf(member.getBirthdate()));
                stmt.setString(7, member.getPresentAddress());
                stmt.setString(8, member.getPermanentAddress());
                stmt.setString(9, member.getContactNumber());
                stmt.setString(10, member.getEmailAddress());
                stmt.setString(11, member.getEmployer());
                stmt.setString(12, member.getEmploymentStatus());
                stmt.setDouble(13, member.getGrossMonthlyIncome());
                stmt.setDouble(14, member.getAverageNetMonthlyIncome());
                stmt.setDate(15, new java.sql.Date(member.getJoinDate().getTime()));
                stmt.setDate(16, new java.sql.Date(member.getLastActivityDate().getTime()));
                
                LocalDateTime now = LocalDateTime.now();
                stmt.setTimestamp(17, Timestamp.valueOf(now));
                stmt.setTimestamp(18, Timestamp.valueOf(now));
                stmt.setString(19, Constants.STATUS_ACTIVE);
                
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
     * Generates a unique member number
     * 
     * @return A unique member number in format MMPC-YYYYNNNNN
     */
    private static String generateMemberNumber() {
        // Get current year
        int year = java.time.LocalDate.now().getYear();
        
        // Get the count of members and add 1
        int memberCount = getMemberCount() + 1;
        
        // Format as MMPC-YYYYNNNNN (e.g., MMPC-202500001)
        return String.format("MMPC-%d%05d", year, memberCount);
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
                    + "member_number = ?, first_name = ?, middle_name = ?, last_name = ?, age = ?, birthdate = ?, "
                    + "present_address = ?, permanent_address = ?, contact_number = ?, email_address = ?, "
                    + "employer = ?, employment_status = ?, gross_monthly_income = ?, average_net_monthly_income = ?, "
                    + "join_date = ?, last_activity_date = ?, updated_at = ?, status = ? "
                    + "WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Generate a unique member number if not provided
                if (member.getMemberNumber() == null || member.getMemberNumber().isEmpty()) {
                    member.setMemberNumber(generateMemberNumber());
                }
                
                stmt.setString(1, member.getMemberNumber());
                stmt.setString(2, member.getFirstName());
                stmt.setString(3, member.getMiddleName());
                stmt.setString(4, member.getLastName());
                stmt.setInt(5, member.getAge());
                stmt.setDate(6, java.sql.Date.valueOf(member.getBirthdate()));
                stmt.setString(7, member.getPresentAddress());
                stmt.setString(8, member.getPermanentAddress());
                stmt.setString(9, member.getContactNumber());
                stmt.setString(10, member.getEmailAddress());
                stmt.setString(11, member.getEmployer());
                stmt.setString(12, member.getEmploymentStatus());
                stmt.setDouble(13, member.getGrossMonthlyIncome());
                stmt.setDouble(14, member.getAverageNetMonthlyIncome());
                
                // Set join date to current date if not provided
                if (member.getJoinDate() == null) {
                    member.setJoinDate(new java.util.Date());
                }
                stmt.setDate(15, new java.sql.Date(member.getJoinDate().getTime()));
                
                // Set last activity date to current date if not provided
                if (member.getLastActivityDate() == null) {
                    member.setLastActivityDate(new java.util.Date());
                }
                stmt.setDate(16, new java.sql.Date(member.getLastActivityDate().getTime()));
                
                stmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(18, member.getStatus());
                stmt.setInt(19, member.getId());
                
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
     * Activates a member by memberNumber
     * 
     * @param memberNumber The member's unique number
     * @return True if successful, false otherwise
     */
    public static boolean activateMember(String memberNumber) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET status = ?, updated_at = ? WHERE member_number = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_ACTIVE);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(3, memberNumber);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error activating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deactivates a member by memberNumber
     * 
     * @param memberNumber The member's unique number
     * @return True if successful, false otherwise
     */
    public static boolean deactivateMember(String memberNumber) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE members SET status = ?, updated_at = ? WHERE member_number = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_INACTIVE);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(3, memberNumber);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deactivating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Registers a new member (compatibility method for views)
     * 
     * @param member The member to register
     * @return True if successful, false otherwise
     */
    public static boolean registerMember(Member member) {
        return createMember(member);
    }
    
    /**
     * Gets a member by member number
     * 
     * @param memberNumber The member number
     * @return The member, or null if not found
     */
    public static Member getMemberByNumber(String memberNumber) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE member_number = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, memberNumber);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return extractMemberFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member by number: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets all active members
     * 
     * @return List of active members
     */
    public static List<Member> getActiveMembers() {
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE status = ? ORDER BY last_name, first_name";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_ACTIVE);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        members.add(extractMemberFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * Gets savings account data for a member
     * 
     * @param memberId The member's ID
     * @return Map containing savings account details for reporting purposes
     */
    public static java.util.Map<String, Object> getMemberSavingsAccounts(int memberId) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Member member = extractMemberFromResultSet(rs);
                        
                        // Since we're using the Member object to store savings data directly,
                        // we'll just return a map with the relevant savings information
                        result.put("memberId", member.getId());
                        result.put("memberNumber", member.getMemberNumber());
                        result.put("memberName", member.getFullName());
                        result.put("balance", member.getSavingsBalance());
                        result.put("interestEarned", member.getInterestEarned());
                        result.put("status", member.getStatus());
                        result.put("lastActivity", member.getLastActivityDate());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member savings accounts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Search for members by keyword
     * 
     * @param keyword The search keyword
     * @return List of matching members
     */
    public static List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMembers(); // Return all if no keyword
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE " +
                    "first_name LIKE ? OR " +
                    "middle_name LIKE ? OR " +
                    "last_name LIKE ? OR " +
                    "member_number LIKE ? OR " +
                    "email_address LIKE ? OR " +
                    "contact_number LIKE ? " +
                    "ORDER BY last_name, first_name";
            
            String searchPattern = "%" + keyword + "%";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
                stmt.setString(5, searchPattern);
                stmt.setString(6, searchPattern);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        members.add(extractMemberFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return members;
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
        
        // Handle the new member_number field
        try {
            member.setMemberNumber(rs.getString("member_number"));
        } catch (SQLException e) {
            // Field might not exist in older database schemas
        }
        
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
        
        // Handle the additional fields
        try {
            java.sql.Date joinDate = rs.getDate("join_date");
            if (joinDate != null) {
                member.setJoinDate(new java.util.Date(joinDate.getTime()));
            }
        } catch (SQLException e) {
            // Field might not exist in older database schemas
        }
        
        try {
            java.sql.Date lastActivityDate = rs.getDate("last_activity_date");
            if (lastActivityDate != null) {
                member.setLastActivityDate(new java.util.Date(lastActivityDate.getTime()));
            }
        } catch (SQLException e) {
            // Field might not exist in older database schemas
        }
        
        return member;
    }
}