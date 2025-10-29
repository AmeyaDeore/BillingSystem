package com.billing.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads application configuration from a properties file in the project root.
 * Falls back to sensible defaults when the file is missing or keys are absent.
 */
public final class AppConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPS = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            PROPS.load(fis);
        } catch (IOException ignored) {
            // Use defaults when file is not present
        }
    }

    private AppConfig() {}

    /**
     * Auth mode can be one of: file, db, hybrid (default: db)
     */
    public static String getAuthMode() {
        String mode = PROPS.getProperty("auth.mode", "db").trim().toLowerCase();
        switch (mode) {
            case "file":
            case "db":
            case "hybrid":
                return mode;
            default:
                return "db";
        }
    }

    public static String getDbUrl() {
        return PROPS.getProperty("db.url", "jdbc:mysql://localhost:3306/electricity_billing");
    }

    public static String getDbUser() {
        return PROPS.getProperty("db.user", "root");
    }

    public static String getDbPassword() {
        return PROPS.getProperty("db.password", "");
    }
}


