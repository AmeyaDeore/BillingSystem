package com.billing.migration;

import com.billing.database.DatabaseConnection;
import com.billing.security.SecurityUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * One-time migration tool: reads users from users.dat and inserts into MySQL.
 * - If value contains 'salt|hash', reuses it.
 * - If value is plaintext, hashes it before inserting.
 *
 * Usage: run main() once with DB configured in config.properties
 */
public class MigrateUsers {

    public static void main(String[] args) throws Exception {
        File f = new File("users.dat");
        if (!f.exists()) {
            System.out.println("users.dat not found. Nothing to migrate.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f));
             Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Database connection not available. Configure config.properties and retry.");
                return;
            }
            String line;
            int migrated = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                String username = parts[0];
                String value = parts[1];
                String toStore;
                int sep = value.indexOf('|');
                if (sep > 0) {
                    // already salt|hash
                    toStore = value;
                } else {
                    // plaintext -> hash now
                    String salt = SecurityUtil.generateSalt();
                    String hash = SecurityUtil.hashPassword(value.toCharArray(), salt);
                    toStore = salt + "|" + hash;
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?) ON DUPLICATE KEY UPDATE password = VALUES(password)")) {
                    ps.setString(1, username);
                    ps.setString(2, toStore);
                    ps.executeUpdate();
                    migrated++;
                }
            }
            System.out.println("Migration complete. Records upserted: " + migrated);
        }
    }
}


