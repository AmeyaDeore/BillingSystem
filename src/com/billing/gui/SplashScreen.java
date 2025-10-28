package com.billing.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow; // A JWindow is a borderless window
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;

/**
 * A simple splash screen that shows for 3 seconds and then opens the LoginFrame.
 * (This is the corrected version and contains NO database logic)
 */
public class SplashScreen extends JWindow { // Note: JWindow, not JFrame

    public SplashScreen() {
        // --- Setup the Splash Screen ---
        setSize(600, 400); // Set the size of the window
        setLocationRelativeTo(null); // Center it on the screen

        // --- Create Content ---
        JLabel contentLabel = new JLabel("<html><center>Electricity Billing System<br>Loading...<br>Version 1.0</center></html>");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentLabel.setOpaque(true);
        contentLabel.setBackground(new Color(240, 240, 240)); // Light gray background
        
        add(contentLabel, BorderLayout.CENTER);

        // --- The "Pause" Logic (using a Thread) ---
        Thread splashThread = new Thread(() -> {
            try {
                // Pause for 3 seconds (3000 milliseconds)
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // After 3 seconds:
            // 1. Close (dispose) this splash screen
            dispose(); 
            
            // 2. Create and show the Login Frame
            // (This is the only thing it should do)
            LoginFrame login = new LoginFrame();
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            login.setVisible(true);
            login.setLocationRelativeTo(null);
            
        });
        
        // Start the thread
        splashThread.start();
    }
}

