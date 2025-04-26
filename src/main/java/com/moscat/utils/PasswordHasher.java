package com.moscat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for password hashing
 */
public class PasswordHasher {
    
    // Hash algorithm
    private static final String ALGORITHM = "SHA-256";
    
    // Salt length in bytes
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hashes a password with a random salt
     * 
     * @param password Plain text password
     * @return Base64-encoded string containing salt and hash
     */
    public static String hash(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password with the salt
            byte[] hash = hashWithSalt(password, salt);
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            
            // Encode as Base64
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * 
     * @param password Plain text password
     * @param storedHash Base64-encoded string containing salt and hash
     * @return true if password matches, false otherwise
     */
    public static boolean verify(String password, String storedHash) {
        try {
            // Decode the stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extract salt and hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, hash, 0, hash.length);
            
            // Hash the input password with the extracted salt
            byte[] computedHash = hashWithSalt(password, salt);
            
            // Compare the computed hash with the stored hash
            return MessageDigest.isEqual(hash, computedHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Hashes a password with a given salt
     * 
     * @param password Plain text password
     * @param salt Salt
     * @return Hash
     * @throws NoSuchAlgorithmException if the algorithm is not available
     */
    private static byte[] hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        // Get a MessageDigest instance for the specified algorithm
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        
        // Add salt to digest
        md.update(salt);
        
        // Hash the password
        return md.digest(password.getBytes());
    }
}