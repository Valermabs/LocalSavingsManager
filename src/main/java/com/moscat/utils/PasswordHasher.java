package com.moscat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 */
public class PasswordHasher {
    
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String DELIMITER = ":";
    
    /**
     * Hashes a password using SHA-256 with a random salt
     * 
     * @param password Password to hash
     * @return Hashed password with salt (format: salt:hash)
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
            
            // Encode salt and hash to Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Return salt and hash concatenated with a delimiter
            return saltBase64 + DELIMITER + hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * 
     * @param password Password to verify
     * @param storedHash Stored hash (format: salt:hash)
     * @return true if password matches, false otherwise
     */
    public static boolean verify(String password, String storedHash) {
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }
            
            // Decode Base64 salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] passwordHash = md.digest(password.getBytes());
            
            // Compare the hashes
            if (hash.length != passwordHash.length) {
                return false;
            }
            
            // Compare each byte
            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != passwordHash[i]) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}