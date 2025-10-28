package com.billing.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main dashboard with logged-in user display and logout functionality.
 */
public class MainDashboard extends JFrame implements ActionListener {

    private Container container;
    private JLabel titleLabel;
    private JLabel userLabel; // Display logged-in user
    private JButton calculateButton;
    private JButton logoutButton;
    private JButton exitButton;
    private String loggedInUser;

    public MainDashboard(String username) {
        this.loggedInUser = username;
        
        setTitle("Main Dashboard - Electricity Billing System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(245, 245, 245));

        // --- Top Panel with title and user info ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 70, 80));
        topPanel.setPreferredSize(new Dimension(800, 80));
        
        titleLabel = new JLabel("Welcome to the Billing Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // User info panel (top right)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        userPanel.setBackground(new Color(60, 70, 80));
        
        userLabel = new JLabel("Logged in as: " + loggedInUser);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this);
        
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(userPanel, BorderLayout.EAST);
        
        container.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel with buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(new Color(245, 250, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        calculateButton = new JButton("Calculate New Bill");
        calculateButton.setFont(new Font("Arial", Font.PLAIN, 18));
        calculateButton.setPreferredSize(new Dimension(250, 60));
        calculateButton.setBackground(new Color(0, 102, 204));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(calculateButton, gbc);

        exitButton = new JButton("Exit Application");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
        exitButton.setPreferredSize(new Dimension(250, 60));
        exitButton.setBackground(new Color(108, 117, 125));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(this);
        gbc.gridy = 1;
        buttonPanel.add(exitButton, gbc);

        container.add(buttonPanel, BorderLayout.CENTER);

        // Bottom spacer for visual balance
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 250, 255));
        bottomPanel.setPreferredSize(new Dimension(800, 16));
        container.add(bottomPanel, BorderLayout.SOUTH);

        System.out.println("MainDashboard created for user: " + loggedInUser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calculateButton) {
            BillingFrame billFrame = new BillingFrame(loggedInUser);
            billFrame.setVisible(true);
            // Dashboard remains open in the background
        } else if (e.getSource() == logoutButton) {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame());
            }
        } else if (e.getSource() == exitButton) {
            int choice = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to exit the application?", 
                "Confirm Exit", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

}

