package com.moscat;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.moscat.utils.DatabaseManager;
import com.moscat.views.LoginView;

/**
 * Main application class
 */
public class App {
    
    /**
     * Application entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
        
        // Initialize the database
        DatabaseManager.getInstance().initializeDatabase();
        
        // Launch the application on the EDT
        SwingUtilities.invokeLater(() -> {
            // Show the login screen
            LoginView loginView = new LoginView(null);
            loginView.setVisible(true);
        });
    }
}