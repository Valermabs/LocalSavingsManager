package com.moscat.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords
 */
public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";
    private static final String SEPARATOR = ":";
    
    /**
     * Hashes a password using a random salt
     * 
     * @param password The password to hash
     * @return The hashed password with salt
     */
    public static String hash(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convert to Base64 strings
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
            
            // Return salt and hash concatenated with a separator
            return saltBase64 + SEPARATOR + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * 
     * @param password The password to verify
     * @param storedHash The stored hash to verify against
     * @return True if the password matches, false otherwise
     */
    public static boolean verify(String password, String storedHash) {
        try {
            // Split the stored hash into salt and hash parts
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                return false;
            }
            
            String saltBase64 = parts[0];
            String storedHashBase64 = parts[1];
            
            // Decode the salt
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            
            // Hash the password with the stored salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
            
            // Compare the computed hash with the stored hash
            return storedHashBase64.equals(hashBase64);
        } catch (Exception e) {
            return false;
        }
    }
}