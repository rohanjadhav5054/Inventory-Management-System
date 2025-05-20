//ItemDialog.java
package dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import database.DBInventoryManager;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Item Dialog for Adding and Editing Inventory Items
 * 
 * This class provides a modal dialog for adding new inventory items or editing existing ones.
 * It presents a form interface with fields for all item properties and handles validation
 * before allowing the operation to complete.
 * 
 * Features:
 * - Dynamic title based on add/edit mode
 * - Form validation with appropriate error messages
 * - Category dropdown populated from existing database categories
 * - Support for adding new categories on-the-fly
 * - Proper cancellation and confirmation handling
 * 
 * Design Principles:
 * - Modal dialog ensures user completes or cancels the operation
 * - Clear visual layout with appropriate spacing
 * - Client-side validation for all fields before database interaction
 * - Reusable for both add and edit operations
 * 
 * @author Inventory Management System Team
 * @version 1.0
 */
public class ItemDialog extends JDialog {
    private JTextField idField, nameField, quantityField, priceField;
    private JComboBox<String> categoryField;
    private boolean confirmed = false;
    private InventoryItem originalItem;
    private DBInventoryManager inventoryManager;
    
    /**
     * Constructs a new item dialog for adding or editing inventory items.
     * 
     * @param parent The parent frame (typically the main application window)
     * @param item The item to edit, or null if adding a new item
     */
    public ItemDialog(JFrame parent, InventoryItem item) {
        super(parent, item == null ? "Add New Item" : "Edit Item", true);
        this.originalItem = item;
        this.inventoryManager = new DBInventoryManager();
        
        // Set up dialog properties
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(parent);
        
        // Create UI
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel(item == null ? "Add New Item" : "Edit Item");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(item == null ? 4 : 5, 2, 10, 10));
        
        // ID field - only shown when editing an existing item
        if (item != null) {
            formPanel.add(new JLabel("ID:"));
            idField = new JTextField(20);
            idField.setEditable(false); // Can't change ID when editing
            formPanel.add(idField);
        } else {
            // Create the field but don't add it to UI
            idField = new JTextField(20);
        }
        
        // Name field
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);
        
        // Quantity field
        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(20);
        formPanel.add(quantityField);
        
        // Price field
        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField(20);
        formPanel.add(priceField);
        
        // Category field with dropdown and scroll
        formPanel.add(new JLabel("Category:"));
        
        // Get all categories from the database
        Set<String> categoriesSet = new HashSet<>();
        for (InventoryItem dbItem : inventoryManager.getAllItems()) {
            if (dbItem != null && dbItem.getCategory() != null && !dbItem.getCategory().trim().isEmpty()) {
                categoriesSet.add(dbItem.getCategory().trim());
            }
        }
        
        // Sort categories for better user experience
        ArrayList<String> sortedCategories = new ArrayList<>(categoriesSet);
        Collections.sort(sortedCategories);
        
        // Create editable combobox with categories
        categoryField = new JComboBox<>(new DefaultComboBoxModel<>());
        
        // Add categories to the combobox
        for (String category : sortedCategories) {
            categoryField.addItem(category);
        }
        
        // Make combobox editable so users can enter new categories
        categoryField.setEditable(true);
        
        // Enable scrolling in dropdown
        categoryField.setMaximumRowCount(5);
        
        formPanel.add(categoryField);
        
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel clicked");
                dispose();
            }
        });
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Save clicked");
                if (validateForm()) {
                    confirmed = true;
                    System.out.println("Form validated successfully, confirmed = " + confirmed);
                    dispose();
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);
        
        // Set values if editing an existing item
        if (item != null) {
            idField.setText(item.getId());
            idField.setEditable(false);  // Can't change ID when editing
            nameField.setText(item.getName());
            quantityField.setText(String.valueOf(item.getQuantity()));
            priceField.setText(String.valueOf(item.getPrice()));
            
            // Set category in combobox
            String currentCategory = item.getCategory();
            boolean categoryExists = false;
            
            // Check if the category already exists in the combobox
            for (int i = 0; i < categoryField.getItemCount(); i++) {
                if (categoryField.getItemAt(i).equals(currentCategory)) {
                    categoryField.setSelectedIndex(i);
                    categoryExists = true;
                    break;
                }
            }
            
            // If not, add it
            if (!categoryExists && currentCategory != null && !currentCategory.isEmpty()) {
                categoryField.addItem(currentCategory);
                categoryField.setSelectedItem(currentCategory);
            }
        }
        
        // Add to dialog
        add(mainPanel);
    }
    
    /**
     * Validates all form fields to ensure they contain valid data.
     * 
     * This method checks:
     * - ID is not empty (for editing only)
     * - Name is not empty
     * - Quantity is a valid non-negative integer
     * - Price is a valid non-negative number
     * - Category is not empty
     * 
     * If any validation fails, an appropriate error message is displayed to the user.
     * 
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        try {
            // Validate ID (only if it's an edit operation)
            if (originalItem != null) {
                String id = idField.getText().trim();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            
            // Validate name
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate quantity
            String quantityStr = quantityField.getText().trim();
            if (quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be a valid number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate price
            String priceStr = priceField.getText().trim();
            if (priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Price cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Price must be a valid number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate category
            String category = categoryField.getSelectedItem() != null ? 
                    categoryField.getSelectedItem().toString().trim() : "";
            if (category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Validation error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Checks whether the dialog was confirmed (Save button clicked after validation passed).
     * 
     * @return true if the dialog was confirmed, false if it was cancelled
     */
    public boolean isConfirmed() {
        System.out.println("isConfirmed called, returning: " + confirmed);
        return confirmed;
    }
    
    /**
     * Returns the inventory item created or edited by this dialog.
     * 
     * This method creates a new InventoryItem object with the values entered in the dialog.
     * If the dialog was cancelled (confirmed == false), returns null.
     * 
     * @return The created or edited InventoryItem, or null if cancelled
     */
    public InventoryItem getItem() {
        if (!confirmed) {
            System.out.println("getItem called but dialog was not confirmed - returning null");
            return null;
        }
        
        try {
            // For new items, we'll use an empty string as ID (will be generated later)
            // For editing, use the existing ID
            String id = originalItem != null ? idField.getText().trim() : "";
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String category = categoryField.getSelectedItem().toString().trim();
            
            InventoryItem item = new InventoryItem(id, name, quantity, price, category);
            System.out.println("Created item from dialog: " + item);
            return item;
        } catch (Exception e) {
            System.out.println("Error creating item in dialog: " + e.getMessage());
            return null;
        }
    }
} 