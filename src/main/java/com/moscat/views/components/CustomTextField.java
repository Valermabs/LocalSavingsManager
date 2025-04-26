package com.moscat.views.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Custom text field with placeholder text
 */
public class CustomTextField extends JTextField {
    
    private String placeholder = "";
    private Color placeholderColor = Color.GRAY;
    private Color textColor = Color.BLACK;
    private boolean showPlaceholder = true;
    
    /**
     * Default constructor
     */
    public CustomTextField() {
        super();
        setupFocusListener();
    }
    
    /**
     * Constructor with initial text
     * 
     * @param text Initial text
     */
    public CustomTextField(String text) {
        super(text);
        setupFocusListener();
    }
    
    /**
     * Constructor with initial text and columns
     * 
     * @param text Initial text
     * @param columns Number of columns
     */
    public CustomTextField(String text, int columns) {
        super(text, columns);
        setupFocusListener();
    }
    
    /**
     * Constructor with document, text, and columns
     * 
     * @param doc Document
     * @param text Initial text
     * @param columns Number of columns
     */
    public CustomTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        setupFocusListener();
    }
    
    /**
     * Constructor with columns
     * 
     * @param columns Number of columns
     */
    public CustomTextField(int columns) {
        super(columns);
        setupFocusListener();
    }
    
    /**
     * Sets up focus listener for placeholder behavior
     */
    private void setupFocusListener() {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder) && showPlaceholder) {
                    setText("");
                    setForeground(textColor);
                    showPlaceholder = false;
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(placeholderColor);
                    showPlaceholder = true;
                }
            }
        });
    }
    
    /**
     * Sets the placeholder text
     * 
     * @param placeholder Placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        if (getText().isEmpty() || getText().equals(this.placeholder)) {
            setText(placeholder);
            setForeground(placeholderColor);
            showPlaceholder = true;
        }
    }
    
    /**
     * Gets the placeholder text
     * 
     * @return Placeholder text
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Sets the placeholder color
     * 
     * @param placeholderColor Placeholder color
     */
    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        if (showPlaceholder) {
            setForeground(placeholderColor);
        }
    }
    
    /**
     * Gets the placeholder color
     * 
     * @return Placeholder color
     */
    public Color getPlaceholderColor() {
        return placeholderColor;
    }
    
    /**
     * Sets the text color (when not showing placeholder)
     * 
     * @param textColor Text color
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        if (!showPlaceholder) {
            setForeground(textColor);
        }
    }
    
    /**
     * Gets the text color
     * 
     * @return Text color
     */
    public Color getTextColor() {
        return textColor;
    }
    
    /**
     * Gets the text, but returns empty string if showing placeholder
     * 
     * @return Text or empty string if showing placeholder
     */
    @Override
    public String getText() {
        String text = super.getText();
        return (text.equals(placeholder) && showPlaceholder) ? "" : text;
    }
    
    /**
     * Gets the raw text, including placeholder if showing
     * 
     * @return Raw text
     */
    public String getRawText() {
        return super.getText();
    }
}