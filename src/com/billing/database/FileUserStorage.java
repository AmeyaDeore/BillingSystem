package com.billing.database;

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
     * Authenticate a user
     */
    public static boolean authenticate(String username, String password) {
        String storedPassword = users.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
    
    /**
     * Register a new user
     */
    public static boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }
        users.put(username, password);
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
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
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
