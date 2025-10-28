package com.billing.gui;

import com.billing.database.DatabaseConnection;
import com.billing.database.FileUserStorage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton skipButton;
    private JLabel errorLabel; // For showing error messages
    private boolean useDatabaseMode = false;
    
    public LoginFrame() {
        setTitle("Login - Electricity Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 10));
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setMaximumSize(new Dimension(350, 40));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 10));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setMaximumSize(new Dimension(350, 40));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Error label for inline error messages
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setMaximumSize(new Dimension(350, 30));
        
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(150, 40));
        
        signupButton = new JButton("Sign Up (Create Account)");
        signupButton.setBackground(new Color(34, 139, 34));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("Arial", Font.PLAIN, 14));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.setMaximumSize(new Dimension(200, 35));
        
        skipButton = new JButton("Skip Login (Test Mode)");
        skipButton.setBackground(new Color(108, 117, 125));
        skipButton.setForeground(Color.WHITE);
        skipButton.setFocusPainted(false);
        skipButton.setFont(new Font("Arial", Font.PLAIN, 12));
        skipButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        skipButton.setMaximumSize(new Dimension(200, 30));
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(errorLabel); // Add error label
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(signupButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(skipButton);
        
        add(mainPanel);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
        
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignupFrame().setVisible(true);
            }
        });
        
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
        
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    MainDashboard dashboard = new MainDashboard("Guest");
                    dashboard.setVisible(true);
                });
            }
        });
        
        System.out.println("LoginFrame created (File Storage Mode)");
        setVisible(true);
    }
    
    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Clear previous error
        errorLabel.setText(" ");
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password!");
            return;
        }
        
        // Check if user exists
        boolean userExists = FileUserStorage.userExists(username);
        
        // Try file-based authentication
        if (FileUserStorage.authenticate(username, password)) {
            showSuccessAndNavigate(username);
            return;
        }
        
        // If file auth fails, try database
        if (tryDatabaseAuth(username, password)) {
            return;
        }
        
        // Authentication failed - show appropriate error
        if (!userExists) {
            errorLabel.setText("Username and password are incorrect!");
        } else {
            errorLabel.setText("Password is incorrect!");
        }
        
        // Clear password field
        passwordField.setText("");
        usernameField.requestFocus();
    }
    
    private void showSuccessAndNavigate(String username) {
        // Create a temporary non-modal dialog for 2 seconds
        JDialog successDialog = new JDialog(this, "Success", false);
        successDialog.setSize(300, 150);
        successDialog.setLocationRelativeTo(this);
        successDialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 139, 34));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("Login Successful!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        
        JLabel subLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subLabel.setForeground(Color.WHITE);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(subLabel, BorderLayout.SOUTH);
        
        successDialog.setContentPane(panel);
        successDialog.setVisible(true);
        
        // Auto-close after 2 seconds and navigate
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                successDialog.dispose();
                dispose();
                SwingUtilities.invokeLater(() -> {
                    MainDashboard dashboard = new MainDashboard(username);
                    dashboard.setVisible(true);
                });
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private boolean tryDatabaseAuth(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnection.getConnection();
            
            if (connection == null) {
                return false;
            }
            
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                showSuccessAndNavigate(username);
                return true;
            }
            
        } catch (SQLException ex) {
            System.err.println("Database auth failed: " + ex.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}

