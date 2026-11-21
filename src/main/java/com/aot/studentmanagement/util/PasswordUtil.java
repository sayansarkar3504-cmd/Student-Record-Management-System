package com.aot.studentmanagement.util;

/**
 * Utility class for password operations.
 */
public class PasswordUtil {

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Verifies a password by comparing the input with the stored value.
     * <p>
     * NOTE: This is a simple string comparison for demonstration purposes only.
     * In a production environment, passwords should be hashed using a secure
     * algorithm such as BCrypt, SCrypt, or Argon2, and verification should
     * compare the hash of the input against the stored hash.
     * </p>
     *
     * @param input  the password entered by the user
     * @param stored the stored password to compare against
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String input, String stored) {
        if (input == null || stored == null) {
            return false;
        }
        return input.equals(stored);
    }
}
