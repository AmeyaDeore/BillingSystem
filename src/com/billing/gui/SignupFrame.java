package com.billing.gui;

import com.billing.database.DatabaseConnection;
import com.billing.database.FileUserStorage;
import com.billing.security.SecurityUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User registration/signup window.
 * Allows new users to create an account.
 */
public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton backButton;
    private JLabel errorLabel; // Add error label
    
    public SignupFrame() {
        setTitle("Sign Up - Create New Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(34, 139, 34));
        
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
        
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBackground(Color.WHITE);
        confirmPanel.setMaximumSize(new Dimension(350, 40));
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPanel.add(confirmLabel, BorderLayout.WEST);
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setMaximumSize(new Dimension(350, 30));
        
        signupButton = new JButton("Create Account");
        signupButton.setBackground(new Color(34, 139, 34));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.setMaximumSize(new Dimension(180, 40));
        
        backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(150, 35));
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(confirmPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(errorLabel); // Add error label
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(signupButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(backButton);
        
        add(mainPanel);
        
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });
    }
    
    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Clear previous error
        errorLabel.setText(" ");
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            return;
        }
        
        if (username.length() < 3) {
            errorLabel.setText("Username must be at least 3 characters long!");
            usernameField.requestFocus();
            return;
        }
        
        if (password.length() < 4) {
            errorLabel.setText("Password must be at least 4 characters long!");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match!");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return;
        }
        
        // Use file-based storage
        if (FileUserStorage.userExists(username)) {
            errorLabel.setText("Username already exists! Choose a different username.");
            usernameField.setText("");
            usernameField.requestFocus();
            return;
        }
        
        if (FileUserStorage.registerUser(username, password)) {
            // Also try to persist to DB (best-effort)
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String salt = SecurityUtil.generateSalt();
                    String hash = SecurityUtil.hashPassword(password.toCharArray(), salt);
                    String value = salt + "|" + hash;
                    try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?) ON DUPLICATE KEY UPDATE password = VALUES(password)")) {
                        ps.setString(1, username);
                        ps.setString(2, value);
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                System.err.println("Warning: DB write for signup failed: " + ex.getMessage());
            }
            // Show success message for 2 seconds
            JDialog successDialog = new JDialog(this, "Success", false);
            successDialog.setSize(300, 150);
            successDialog.setLocationRelativeTo(this);
            successDialog.setUndecorated(true);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(34, 139, 34));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel messageLabel = new JLabel("Account Created!", SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
            messageLabel.setForeground(Color.WHITE);
            
            JLabel subLabel = new JLabel("You can now login!", SwingConstants.CENTER);
            subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            subLabel.setForeground(Color.WHITE);
            
            panel.add(messageLabel, BorderLayout.CENTER);
            panel.add(subLabel, BorderLayout.SOUTH);
            
            successDialog.setContentPane(panel);
            successDialog.setVisible(true);
            
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    successDialog.dispose();
                    dispose();
                    new LoginFrame();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            errorLabel.setText("Registration failed. Please try again.");
        }
    }
}
