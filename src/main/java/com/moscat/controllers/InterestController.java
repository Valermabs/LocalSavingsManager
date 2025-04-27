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

import com.moscat.models.InterestSetting;
import com.moscat.models.Member;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

/**
 * Controller for interest-related operations
 */
public class InterestController {
    
    /**
     * Creates a new interest rate setting
     * 
     * @param interestSetting The interest setting to create
     * @return True if successful, false otherwise
     */
    public static boolean createInterestSetting(InterestSetting interestSetting) {
        // Validate that the effective date is not in the past
        if (interestSetting.getEffectiveDate().isBefore(LocalDate.now())) {
            return false;
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "INSERT INTO interest_settings "
                    + "(interest_rate, minimum_balance_required, computation_basis, effective_date, reason_for_change, set_by, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDouble(1, interestSetting.getInterestRate());
                stmt.setDouble(2, interestSetting.getMinimumBalanceRequired());
                stmt.setString(3, interestSetting.getComputationBasis());
                stmt.setDate(4, java.sql.Date.valueOf(interestSetting.getEffectiveDate()));
                stmt.setString(5, interestSetting.getReasonForChange());
                stmt.setString(6, interestSetting.getSetBy());
                stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error creating interest setting: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the current interest setting
     * 
     * @return The current interest setting
     */
    public static InterestSetting getCurrentInterestSetting() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM interest_settings WHERE effective_date <= ? ORDER BY effective_date DESC, id DESC LIMIT 1";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return extractInterestSettingFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting current interest setting: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return default if not found
        InterestSetting defaultSetting = new InterestSetting();
        defaultSetting.setInterestRate(Constants.DEFAULT_SAVINGS_INTEREST_RATE);
        defaultSetting.setMinimumBalanceRequired(Constants.DEFAULT_MINIMUM_BALANCE);
        defaultSetting.setComputationBasis(Constants.INTEREST_COMPUTATION_MONTHLY);
        defaultSetting.setEffectiveDate(LocalDate.now());
        defaultSetting.setReasonForChange("Default setting");
        defaultSetting.setSetBy("System");
        
        return defaultSetting;
    }
    
    /**
     * Gets all interest settings
     * 
     * @return List of all interest settings
     */
    public static List<InterestSetting> getAllInterestSettings() {
        List<InterestSetting> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM interest_settings ORDER BY effective_date DESC, id DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    settings.add(extractInterestSettingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting interest settings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return settings;
    }
    
    /**
     * Calculates interest for all qualifying members
     * 
     * @param processedBy Username of the user processing the interest
     * @return The number of members who received interest
     */
    public static int calculateInterestForAllMembers(String processedBy) {
        int count = 0;
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Get current interest setting
            InterestSetting currentSetting = getCurrentInterestSetting();
            
            // Get all active members
            String query = "SELECT * FROM members WHERE status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_ACTIVE);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Member member = new Member();
                        member.setId(rs.getInt("id"));
                        member.setSavingsBalance(rs.getDouble("savings_balance"));
                        
                        // Calculate interest for this member
                        if (calculateInterestForMember(member, currentSetting, processedBy)) {
                            count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating interest: " + e.getMessage());
            e.printStackTrace();
        }
        
        return count;
    }
    
    /**
     * Calculates interest for a specific member
     * 
     * @param member The member
     * @param interestSetting The interest setting to use
     * @param processedBy Username of the user processing the interest
     * @return True if interest was applied, false otherwise
     */
    public static boolean calculateInterestForMember(Member member, InterestSetting interestSetting, String processedBy) {
        // Check if balance qualifies for interest
        if (!interestSetting.qualifiesForInterest(member.getSavingsBalance())) {
            return false;
        }
        
        // Calculate interest amount
        double interestAmount = interestSetting.calculateInterest(member.getSavingsBalance());
        
        // Record the interest transaction
        String description = String.format("Interest at %.2f%% (%s)", 
                interestSetting.getInterestRate(), 
                interestSetting.getComputationBasis());
        
        return TransactionController.recordInterest(member.getId(), interestAmount, description, processedBy);
    }
    
    /**
     * Extracts an InterestSetting object from a ResultSet
     * 
     * @param rs The ResultSet
     * @return The extracted InterestSetting
     * @throws SQLException If a database error occurs
     */
    private static InterestSetting extractInterestSettingFromResultSet(ResultSet rs) throws SQLException {
        InterestSetting setting = new InterestSetting();
        setting.setId(rs.getInt("id"));
        setting.setInterestRate(rs.getDouble("interest_rate"));
        setting.setMinimumBalanceRequired(rs.getDouble("minimum_balance_required"));
        setting.setComputationBasis(rs.getString("computation_basis"));
        setting.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
        setting.setReasonForChange(rs.getString("reason_for_change"));
        setting.setSetBy(rs.getString("set_by"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            setting.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return setting;
    }
}