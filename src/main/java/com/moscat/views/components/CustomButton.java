package com.moscat.views.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom styled button component
 */
public class CustomButton extends JButton {
    
    private Color defaultBackground = new Color(70, 130, 180); // Steel blue
    private Color hoverBackground = new Color(100, 149, 237);  // Cornflower blue
    private Color pressedBackground = new Color(25, 25, 112);  // Midnight blue
    
    /**
     * Default constructor
     */
    public CustomButton() {
        this("");
    }
    
    /**
     * Constructor with button text
     * 
     * @param text Button text
     */
    public CustomButton(String text) {
        super(text);
        setupUI();
    }
    
    /**
     * Sets up the UI styling and event listeners
     */
    private void setupUI() {
        // Set basic properties
        setFont(new Font("Arial", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setBackground(defaultBackground);
        setFocusPainted(false);
        setBorderPainted(true);
        setContentAreaFilled(true);
        
        // Create a rounded border with padding
        Border line = new LineBorder(new Color(25, 25, 112), 1);
        Border padding = new EmptyBorder(8, 15, 8, 15);
        setBorder(new CompoundBorder(line, padding));
        
        // Add mouse listeners for hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverBackground);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(defaultBackground);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(pressedBackground);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    if (getModel().isRollover()) {
                        setBackground(hoverBackground);
                    } else {
                        setBackground(defaultBackground);
                    }
                }
            }
        });
    }
    
    /**
     * Sets the button color scheme
     * 
     * @param defaultColor Default button color
     * @param hoverColor Color when mouse hovers over button
     * @param pressedColor Color when button is pressed
     */
    public void setButtonColors(Color defaultColor, Color hoverColor, Color pressedColor) {
        this.defaultBackground = defaultColor;
        this.hoverBackground = hoverColor;
        this.pressedBackground = pressedColor;
        setBackground(defaultBackground);
    }
}