package com.billing.main;

import com.billing.gui.SplashScreen;
import javax.swing.SwingUtilities;

/**
 * The main entry point for the entire Electricity Billing System application.
 * Its only job is to create and display the splash screen.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Main: Application starting");
        SwingUtilities.invokeLater(() -> {
            System.out.println("Main: Creating SplashScreen");
            new SplashScreen();
        });
    }
}

