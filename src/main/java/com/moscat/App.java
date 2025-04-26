package com.moscat;

import com.moscat.utils.DatabaseManager;
import com.moscat.views.LoginView;
import javax.swing.*;
import java.awt.*;

/**
 * Main application class
 */
public class App {
    private static JFrame mainFrame;
    
    /**
     * Main method
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set look and feel
            setLookAndFeel();
            
            // Initialize the database
            DatabaseManager.getInstance(); // This will call the singleton constructor which initializes the database
            
            // Create and configure the main frame
            setupMainFrame();
            
            // Show the login screen
            changeView(new LoginView(mainFrame));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * Sets up the main application frame
     */
    private static void setupMainFrame() {
        mainFrame = new JFrame("MOSCAT Multipurpose Cooperative");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1024, 768);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * Changes the current view
     * 
     * @param view New view to display
     */
    public static void changeView(JPanel view) {
        if (mainFrame != null) {
            mainFrame.getContentPane().removeAll();
            mainFrame.getContentPane().add(view, BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
    
    /**
     * Sets the application look and feel
     */
    private static void setLookAndFeel() {
        try {
            // Use the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default font
            Font defaultFont = new Font("Arial", Font.PLAIN, 12);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("CheckBox.font", defaultFont);
            UIManager.put("RadioButton.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", defaultFont);
            UIManager.put("TabbedPane.font", defaultFont);
            
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
    }
}