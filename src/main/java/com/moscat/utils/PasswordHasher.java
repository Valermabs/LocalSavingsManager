package com.moscat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 */
public class PasswordHasher {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // bytes
    private static final String SEPARATOR = ":";
    
    /**
     * Hashes a password using a secure algorithm
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
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Convert salt and hashed password to Base64 for storage
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashedPasswordBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Return the combined salt and hashed password, separated by a delimiter
            return saltBase64 + SEPARATOR + hashedPasswordBase64;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * 
     * @param password The password to verify
     * @param storedHash The stored hash to compare against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verify(String password, String storedHash) {
        try {
            // Split the stored hash to get the salt and hash parts
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                return false; // Invalid format
            }
            
            // Decode the salt and stored hash from Base64
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the password with the same salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] actualHash = md.digest(password.getBytes());
            
            // Compare the expected hash with the actual hash
            if (expectedHash.length != actualHash.length) {
                return false;
            }
            
            // Compare each byte of the hash to ensure they match
            for (int i = 0; i < expectedHash.length; i++) {
                if (expectedHash[i] != actualHash[i]) {
                    return false;
                }
            }
            
            return true;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to verify password", e);
        } catch (IllegalArgumentException e) {
            // Handle Base64 decoding errors
            return false;
        }
    }
}