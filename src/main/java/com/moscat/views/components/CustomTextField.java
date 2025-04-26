package com.moscat.views.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Custom styled text field component
 */
public class CustomTextField extends JTextField {
    
    /**
     * Default constructor
     */
    public CustomTextField() {
        this("");
    }
    
    /**
     * Constructor with initial text
     * 
     * @param text Initial text
     */
    public CustomTextField(String text) {
        super(text);
        setupUI();
    }
    
    /**
     * Sets up the UI styling
     */
    private void setupUI() {
        setFont(new Font("Arial", Font.PLAIN, 14));
        setBackground(Color.WHITE);
        
        // Create a rounded border with padding
        Border line = new LineBorder(new Color(180, 180, 180), 1);
        Border padding = new EmptyBorder(5, 8, 5, 8);
        setBorder(new CompoundBorder(line, padding));
    }
}