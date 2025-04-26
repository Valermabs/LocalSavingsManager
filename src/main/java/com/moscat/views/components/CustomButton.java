package com.moscat.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom button with hover effects
 */
public class CustomButton extends JButton {
    
    private Color defaultColor = new Color(0, 87, 146); // Dark blue
    private Color hoverColor = new Color(0, 120, 200);  // Lighter blue
    private Color pressedColor = new Color(0, 60, 100); // Darker blue
    private Color textColor = Color.WHITE;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int roundness = 10;
    
    /**
     * Default constructor
     */
    public CustomButton() {
        super();
        setupButton();
    }
    
    /**
     * Constructor with text
     * 
     * @param text Button text
     */
    public CustomButton(String text) {
        super(text);
        setupButton();
    }
    
    /**
     * Constructor with icon
     * 
     * @param icon Button icon
     */
    public CustomButton(Icon icon) {
        super(icon);
        setupButton();
    }
    
    /**
     * Constructor with text and icon
     * 
     * @param text Button text
     * @param icon Button icon
     */
    public CustomButton(String text, Icon icon) {
        super(text, icon);
        setupButton();
    }
    
    /**
     * Sets up button appearance and behavior
     */
    private void setupButton() {
        setForeground(textColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setFont(new Font("Arial", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add mouse listeners for hover and press effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    /**
     * Sets the default button color
     * 
     * @param color Default color
     */
    public void setDefaultColor(Color color) {
        this.defaultColor = color;
        repaint();
    }
    
    /**
     * Gets the default button color
     * 
     * @return Default color
     */
    public Color getDefaultColor() {
        return defaultColor;
    }
    
    /**
     * Sets the hover color
     * 
     * @param color Hover color
     */
    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }
    
    /**
     * Gets the hover color
     * 
     * @return Hover color
     */
    public Color getHoverColor() {
        return hoverColor;
    }
    
    /**
     * Sets the pressed color
     * 
     * @param color Pressed color
     */
    public void setPressedColor(Color color) {
        this.pressedColor = color;
        repaint();
    }
    
    /**
     * Gets the pressed color
     * 
     * @return Pressed color
     */
    public Color getPressedColor() {
        return pressedColor;
    }
    
    /**
     * Sets the text color
     * 
     * @param color Text color
     */
    @Override
    public void setForeground(Color color) {
        this.textColor = color;
        super.setForeground(color);
    }
    
    /**
     * Sets the roundness of the button corners
     * 
     * @param roundness Roundness in pixels
     */
    public void setRoundness(int roundness) {
        this.roundness = roundness;
        repaint();
    }
    
    /**
     * Gets the roundness of the button corners
     * 
     * @return Roundness in pixels
     */
    public int getRoundness() {
        return roundness;
    }
    
    /**
     * Paints the button with custom appearance
     * 
     * @param g Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Determine button color based on state
        if (isPressed) {
            g2d.setColor(pressedColor);
        } else if (isHovered) {
            g2d.setColor(hoverColor);
        } else {
            g2d.setColor(defaultColor);
        }
        
        // Draw rounded rectangle
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), roundness, roundness);
        
        g2d.dispose();
        
        super.paintComponent(g);
    }
}