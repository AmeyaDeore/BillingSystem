package com.billing.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A utility class to manage the connection to the MySQL database.
 * This class follows the Singleton pattern by providing a single static
 * method to get the connection, ensuring we manage resources efficiently.
 */
public class DatabaseConnection {

    // --- Configuration ---
    private static final String URL = "jdbc:mysql://localhost:3306/electricity_billing";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Change this to your MySQL password

    /**
     * Attempts to create and return a connection to the database.
     * @return A Connection object.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            System.out.println("Attempting to load MySQL driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully.");
            
            System.out.println("Attempting to connect to database...");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            System.err.println("Please ensure mysql-connector-java.jar is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection to database failed!");
            System.err.println("URL: " + URL);
            System.err.println("Username: " + USERNAME);
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}

