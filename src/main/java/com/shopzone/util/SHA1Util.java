package com.shopzone.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA1 password hashing utility.
 */
public class SHA1Util {

    /**
     * Hash a plaintext string using SHA-1 algorithm.
     * Returns lowercase hexadecimal string.
     */
    public static String hash(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }

    /**
     * Verify a plaintext password against a SHA-1 hash.
     */
    public static boolean verify(String plaintext, String hash) {
        if (plaintext == null || hash == null) return false;
        return hash(plaintext).equalsIgnoreCase(hash);
    }
}
