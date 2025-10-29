package com.billing.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Minimal password hashing helper using PBKDF2WithHmacSHA256.
 */
public final class SecurityUtil {

    private static final String ALGO = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16; // 128-bit salt
    private static final int HASH_BYTES = 32; // 256-bit hash
    private static final int ITERATIONS = 100_000;

    private SecurityUtil() {}

    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(char[] password, String base64Salt) {
        byte[] salt = Base64.getDecoder().decode(base64Salt);
        byte[] hash = pbkdf2(password, salt, ITERATIONS, HASH_BYTES);
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(char[] password, String base64Salt, String expectedBase64Hash) {
        String computed = hashPassword(password, base64Salt);
        return constantTimeEquals(expectedBase64Hash, computed);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int length) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 not available", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes();
        byte[] y = b.getBytes();
        if (x.length != y.length) return false;
        int diff = 0;
        for (int i = 0; i < x.length; i++) {
            diff |= x[i] ^ y[i];
        }
        return diff == 0;
    }
}


