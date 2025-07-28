package org.example.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Hasher {

    private static final int LOG_ROUNDS = 12;

    public static String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean validate(String password, String hashedPassword) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }

        return BCrypt.checkpw(password, hashedPassword);
    }
}
