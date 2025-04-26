package com.moscat;

import com.moscat.utils.DatabaseManager;
import com.moscat.views.LoginView;

import javax.swing.*;
import java.awt.*;

/**
 * Main Application Class for MOSCAT Multipurpose Cooperative
 * Savings and Loan System
 */
public class App {
    private static JFrame mainFrame;
    
    public static void main(String[] args) {
        try {
            // Set Look and Feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Initialize database
            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.initializeDatabase();
            
            // Setup main application frame
            SwingUtilities.invokeLater(() -> {
                mainFrame = new JFrame("MOSCAT Multipurpose Cooperative");
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setSize(1200, 800);
                
                // Center the frame on screen
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width - mainFrame.getWidth()) / 2;
                int y = (screenSize.height - mainFrame.getHeight()) / 2;
                mainFrame.setLocation(x, y);
                
                // Set application icon
                ImageIcon icon = new ImageIcon(App.class.getResource("/moscat_logo.png"));
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    mainFrame.setIconImage(icon.getImage());
                }
                
                // Start with login view
                LoginView loginView = new LoginView(mainFrame);
                mainFrame.setContentPane(loginView);
                
                // Display the frame
                mainFrame.setVisible(true);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Application Error", 
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Changes the current view in the main application frame
     * 
     * @param panel The new panel to display
     */
    public static void changeView(JPanel panel) {
        if (mainFrame != null) {
            mainFrame.setContentPane(panel);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
    
    /**
     * Returns the main application frame
     * 
     * @return JFrame The main application frame
     */
    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
