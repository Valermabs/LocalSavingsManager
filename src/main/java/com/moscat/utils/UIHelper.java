package com.moscat.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Helper methods for UI components
 */
public class UIHelper {
    
    /**
     * Creates a formatted number field for currency input
     * 
     * @return Formatted JFormattedTextField
     */
    public static JFormattedTextField createCurrencyField() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setValue(0.0);
        return field;
    }
    
    /**
     * Creates a formatted number field for integer input
     * 
     * @return Formatted JFormattedTextField
     */
    public static JFormattedTextField createIntegerField() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setValue(0);
        return field;
    }
    
    /**
     * Creates a formatted number field for percentage input
     * 
     * @return Formatted JFormattedTextField
     */
    public static JFormattedTextField createPercentageField() {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setMaximum(1.0);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setValue(0.0);
        return field;
    }
    
    /**
     * Creates a formatted number field for decimal input
     * 
     * @param min Minimum value
     * @param max Maximum value
     * @param decimalPlaces Number of decimal places
     * @return Formatted JFormattedTextField
     */
    public static JFormattedTextField createDecimalField(double min, double max, int decimalPlaces) {
        StringBuilder pattern = new StringBuilder("0");
        if (decimalPlaces > 0) {
            pattern.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                pattern.append("0");
            }
        }
        
        DecimalFormat format = new DecimalFormat(pattern.toString());
        format.setMinimumFractionDigits(decimalPlaces);
        format.setMaximumFractionDigits(decimalPlaces);
        
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setValue(min);
        return field;
    }
    
    /**
     * Creates a panel with title and content
     * 
     * @param title Panel title
     * @param content Panel content
     * @return Panel with title and content
     */
    public static JPanel createTitledPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates a label with a specific font size
     * 
     * @param text Label text
     * @param fontSize Font size
     * @return Label with specified font size
     */
    public static JLabel createLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, fontSize));
        return label;
    }
    
    /**
     * Creates a label with a specific font style and size
     * 
     * @param text Label text
     * @param fontStyle Font style (e.g., Font.BOLD)
     * @param fontSize Font size
     * @return Label with specified font style and size
     */
    public static JLabel createLabel(String text, int fontStyle, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), fontStyle, fontSize));
        return label;
    }
    
    /**
     * Creates a form field with label
     * 
     * @param labelText Label text
     * @param field Form field
     * @return Panel with label and field
     */
    public static JPanel createFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel label = new JLabel(labelText + ":");
        label.setPreferredSize(new Dimension(150, label.getPreferredSize().height));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        return panel;
    }
    
    /**
     * Creates a button with an action listener
     * 
     * @param text Button text
     * @param listener Action listener
     * @return Button with action listener
     */
    public static JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }
    
    /**
     * Creates a button panel with alignment
     * 
     * @param buttons Buttons to add
     * @param alignment Alignment (e.g., FlowLayout.RIGHT)
     * @return Button panel
     */
    public static JPanel createButtonPanel(JButton[] buttons, int alignment) {
        JPanel panel = new JPanel(new FlowLayout(alignment, 10, 0));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }
    
    /**
     * Sets up a JTable with alternating row colors
     * 
     * @param table JTable to set up
     * @param model TableModel for the table
     */
    public static void setupTable(JTable table, DefaultTableModel model) {
        table.setModel(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Set alternating row colors
        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }
                
                return c;
            }
        });
    }
    
    /**
     * Centers a window on the screen
     * 
     * @param window Window to center
     */
    public static void centerWindow(Window window) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
        window.setLocation(x, y);
    }
    
    /**
     * Shows a confirmation dialog
     * 
     * @param parent Parent component
     * @param title Dialog title
     * @param message Dialog message
     * @return true if confirmed, false otherwise
     */
    public static boolean showConfirmDialog(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Shows an error dialog
     * 
     * @param parent Parent component
     * @param title Dialog title
     * @param message Error message
     */
    public static void showErrorDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an information dialog
     * 
     * @param parent Parent component
     * @param title Dialog title
     * @param message Information message
     */
    public static void showInfoDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}