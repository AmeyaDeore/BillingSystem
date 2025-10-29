package com.billing.database;

import com.billing.security.SecurityUtil;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple file-based user storage system.
 * No MySQL required - stores users in a text file.
 */
public class FileUserStorage {
    private static final String USER_FILE = "users.dat";
    private static Map<String, String> users = new HashMap<>();
    
    static {
        loadUsers();
        // Add default admin if no users exist
        if (users.isEmpty()) {
            users.put("admin", "12345");
            saveUsers();
        }
    }
    
    /**
     * Authenticate a user. Supports both legacy plaintext (value = password)
     * and hashed format (value = salt|hash).
     */
    public static boolean authenticate(String username, String password) {
        String stored = users.get(username);
        if (stored == null) return false;
        int sep = stored.indexOf('|');
        if (sep > 0) {
            String salt = stored.substring(0, sep);
            String hash = stored.substring(sep + 1);
            return SecurityUtil.verifyPassword(password.toCharArray(), salt, hash);
        }
        // legacy plaintext
        return stored.equals(password);
    }
    
    /**
     * Register a new user. Stores salted+hashed credentials in the format salt|hash
     */
    public static boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }
        String salt = SecurityUtil.generateSalt();
        String hash = SecurityUtil.hashPassword(password.toCharArray(), salt);
        users.put(username, salt + "|" + hash);
        saveUsers();
        return true;
    }
    
    /**
     * Check if username exists
     */
    public static boolean userExists(String username) {
        return users.containsKey(username);
    }
    
    /**
     * Load users from file
     */
    private static void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                String username = parts[0];
                String value = parts[1];
                users.put(username, value);
            }
            System.out.println("Loaded " + users.size() + " users from file");
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    /**
     * Save users to file
     */
    private static void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
            System.out.println("Saved " + users.size() + " users to file");
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}
