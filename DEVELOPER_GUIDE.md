# Inventory Management System - Developer Guide

This guide is intended for developers who want to maintain, enhance, or extend the Inventory Management System. It provides detailed information about the system architecture, code organization, and best practices for making changes.

## Table of Contents
1. [System Architecture](#system-architecture)
2. [Code Organization](#code-organization)
3. [Key Classes](#key-classes)
4. [Database Design](#database-design)
5. [Adding New Features](#adding-new-features)
6. [Coding Standards](#coding-standards)
7. [Testing](#testing)
8. [Common Tasks](#common-tasks)

## System Architecture

The Inventory Management System follows a layered architecture pattern with clear separation of concerns:

### Presentation Layer
- Implemented using Java Swing components
- Contains all UI-related code for screens, dialogs, and visual elements
- Located primarily in the `loginpage` and `dashboard` packages

### Business Logic Layer
- Contains the core business logic for inventory management
- Bridges the UI and data access layers
- Located in the `dashboard` package for legacy reasons (InventoryManager class)

### Data Access Layer
- Handles all database operations and data persistence
- Isolates the business logic from database implementation details
- Located in the `database` package

### Database Layer
- MySQL database for persistent storage
- Accessed through JDBC

## Code Organization

The application code is organized into three main packages:

### `src/loginpage`
Contains components related to user authentication and the login screen:
- `LoginPage.java`: The main login screen UI
- `RoundedTextField.java`: Custom rounded text field component
- `RoundedPasswordField.java`: Custom rounded password field component
- `RoundedButton.java`: Custom rounded button component

### `src/dashboard`
Contains the main inventory management UI and legacy business logic:
- `InventoryDashboard.java`: The main application UI
- `InventoryItem.java`: Data model for inventory items
- `InventoryManager.java`: Legacy in-memory inventory manager (partially replaced by DBInventoryManager)
- `ItemDialog.java`: Dialog for adding/editing items
- `RecentItemsPanel.java`: UI component for showing recent items

### `src/database`
Contains database connection and data access components:
- `DBConnection.java`: Manages database connections
- `DBInventoryManager.java`: MySQL implementation of inventory manager
- `DatabaseInitializer.java`: Sets up database schema and initializes sample data

### Entry Point
- `src/Main.java`: Application entry point that initializes the database and starts the UI

## Key Classes

### `InventoryItem` (Model)
The core data model class representing an inventory item with properties like id, name, quantity, price, and category.

```java
// Creating a new inventory item
InventoryItem item = new InventoryItem("ITM006", "Keyboard", 15, 49.99, "Electronics");
```

### `DBInventoryManager` (Data Access)
Handles all database operations for inventory items, including CRUD operations and search functionalities.

```java
// Using the DBInventoryManager to add an item
DBInventoryManager dbManager = new DBInventoryManager();
boolean success = dbManager.addItem(new InventoryItem("ITM006", "Keyboard", 15, 49.99, "Electronics"));
```

### `InventoryDashboard` (UI Controller)
The main application UI that displays inventory items and provides access to all features.

### `DBConnection` (Database Utility)
Manages database connections using a simple connection pool pattern.

## Database Design

The application uses a simple database schema with a single table:

### inventory_items
| Column   | Type        | Description                   |
|----------|-------------|-------------------------------|
| id       | VARCHAR(10) | Primary key, unique identifier|
| name     | VARCHAR(100)| Item name/description         |
| quantity | INT         | Current stock quantity        |
| price    | DOUBLE      | Unit price                    |
| category | VARCHAR(50) | Category classification       |

## Adding New Features

### Adding a New Item Attribute

1. Update the `InventoryItem` class:
   ```java
   // Add the new attribute
   private String manufacturer;
   
   // Add to constructor
   public InventoryItem(String id, String name, int quantity, double price, String category, String manufacturer) {
       // existing code...
       this.manufacturer = manufacturer;
   }
   
   // Add getter and setter
   public String getManufacturer() { return manufacturer; }
   public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
   ```

2. Update the database schema in `DBInventoryManager.initializeDatabase()`:
   ```java
   String createTableSQL = 
       "CREATE TABLE IF NOT EXISTS inventory_items (" +
       "id VARCHAR(10) PRIMARY KEY, " +
       "name VARCHAR(100) NOT NULL, " +
       "quantity INT NOT NULL, " +
       "price DOUBLE NOT NULL, " +
       "category VARCHAR(50) NOT NULL, " +
       "manufacturer VARCHAR(100)" +  // New column
       ")";
   ```

3. Update the `DBInventoryManager` methods to handle the new attribute:
   ```java
   // In addItem method
   pstmt.setString(6, item.getManufacturer());
   
   // In updateItem method
   pstmt.setString(5, item.getManufacturer());
   pstmt.setString(6, item.getId());
   
   // In extractItemFromResultSet method
   String manufacturer = rs.getString("manufacturer");
   return new InventoryItem(id, name, quantity, price, category, manufacturer);
   ```

4. Update the `ItemDialog` to include the new field:
   ```java
   // Add a new text field
   private JTextField manufacturerField;
   
   // Initialize in constructor
   manufacturerField = new JTextField(20);
   
   // Add to form panel
   formPanel.add(createFormGroup("Manufacturer:", manufacturerField));
   
   // Include in item creation/update
   String manufacturer = manufacturerField.getText();
   item = new InventoryItem(id, name, quantity, price, category, manufacturer);
   ```

### Adding a New Report

1. Add a new method in `InventoryDashboard`:
   ```java
   private void generateManufacturerReport() {
       StringBuilder report = new StringBuilder("MANUFACTURER REPORT\n");
       report.append("========================================\n\n");
       
       // Build report content
       // ...
       
       JTextArea textArea = new JTextArea(report.toString());
       textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
       textArea.setEditable(false);
       
       JScrollPane scrollPane = new JScrollPane(textArea);
       scrollPane.setPreferredSize(new Dimension(600, 500));

       JOptionPane.showMessageDialog(this, scrollPane, "Manufacturer Report", JOptionPane.PLAIN_MESSAGE);
   }
   ```

2. Add a menu item in `createMenuBar()`:
   ```java
   JMenuItem manufacturerReport = createStyledMenuItem("Manufacturer Report", menuFont, e -> generateManufacturerReport());
   reportsMenu.add(manufacturerReport);
   ```

## Coding Standards

When making changes to the Inventory Management System, please follow these coding standards:

1. **Java Naming Conventions**:
   - Classes: UpperCamelCase (e.g., `InventoryItem`)
   - Methods and variables: lowerCamelCase (e.g., `getQuantity()`)
   - Constants: UPPER_SNAKE_CASE (e.g., `MAX_HISTORY_ITEMS`)

2. **Documentation**:
   - Add JavaDoc comments for all new classes and methods
   - Include parameter descriptions and return values
   - Document any non-obvious behaviors or edge cases

3. **Error Handling**:
   - Use try-catch blocks for database operations and other exception-prone code
   - Log errors with appropriate messages
   - Display user-friendly error messages in the UI

4. **Database Operations**:
   - Always use prepared statements for SQL queries with parameters
   - Close all database resources (Connections, Statements, ResultSets) properly
   - Use try-with-resources to ensure proper resource management

5. **UI Design**:
   - Follow the existing UI style and components
   - Maintain a consistent look and feel across the application
   - Use the custom rounded components for text fields and buttons

## Testing

### Manual Testing
For each change, manually test:
1. The specific feature you've added or modified
2. Any related features that might be affected
3. Check for edge cases and error conditions

### Database Testing
When making changes to database operations:
1. Test with both empty and populated database
2. Test with invalid or edge-case inputs
3. Verify that data is persisted correctly

## Common Tasks

### Adding a New Dialog

1. Create a new dialog class extending JDialog:
   ```java
   public class MyNewDialog extends JDialog {
       private boolean confirmed = false;
       
       public MyNewDialog(JFrame parent) {
           super(parent, "Dialog Title", true);
           initComponents();
       }
       
       private void initComponents() {
           // UI components initialization
           // ...
       }
       
       public boolean isConfirmed() {
           return confirmed;
       }
   }
   ```

2. Create a method in `InventoryDashboard` to show the dialog:
   ```java
   private void showMyNewDialog() {
       MyNewDialog dialog = new MyNewDialog(this);
       dialog.setVisible(true);
       
       if (dialog.isConfirmed()) {
           // Handle dialog result
           // ...
       }
   }
   ```

3. Add a menu item or button to trigger the dialog:
   ```java
   JMenuItem newDialogItem = createStyledMenuItem("New Dialog", menuFont, e -> showMyNewDialog());
   someMenu.add(newDialogItem);
   ```

### Modifying the Database Schema

1. Update the `initializeDatabase()` method in `DBInventoryManager`:
   ```java
   public void initializeDatabase() {
       String createTableSQL = 
           "CREATE TABLE IF NOT EXISTS inventory_items (" +
           "id VARCHAR(10) PRIMARY KEY, " +
           "name VARCHAR(100) NOT NULL, " +
           // Add or modify columns here
           ")";
       
       // Execute the SQL
       // ...
   }
   ```

2. For existing databases, create a migration method:
   ```java
   public void migrateDatabase() {
       String alterTableSQL = 
           "ALTER TABLE inventory_items " +
           "ADD COLUMN new_column VARCHAR(100)";
       
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
           
           stmt.execute(alterTableSQL);
           System.out.println("Database migrated successfully");
           
       } catch (SQLException e) {
           System.err.println("Error migrating database: " + e.getMessage());
           e.printStackTrace();
       }
   }
   ```

3. Call the migration method from the `DatabaseInitializer`:
   ```java
   public static void initialize() {
       // existing code...
       
       DBInventoryManager dbManager = new DBInventoryManager();
       dbManager.initializeDatabase();
       dbManager.migrateDatabase(); // Add this line
       dbManager.initializeSampleData();
       
       // existing code...
   }
   ``` 