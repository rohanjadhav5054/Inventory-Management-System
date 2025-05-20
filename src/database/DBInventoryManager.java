//DBInventoryManager.java
package database;

import dashboard.InventoryItem;

import java.sql.*;
import java.util.LinkedList;

/**
 * Database-backed Inventory Manager
 * 
 * This class provides the implementation for inventory management operations using MySQL database.
 * It serves as the Data Access Layer for inventory items, handling all CRUD (Create, Read, Update, Delete)
 * operations between the application and the database.
 * <p>
 * Key responsibilities include:
 * <ul>
 *     <li>Creating and initializing the database schema</li>
 *     <li>Adding sample data when the database is empty</li>
 *     <li>Performing all inventory item operations (add, update, delete, search)</li>
 *     <li>Converting between database records and InventoryItem objects</li>
 *     <li>Handling database errors and providing appropriate feedback</li>
 * </ul>
 * <p>
 * This class replaces the in-memory implementation (InventoryManager) to provide
 * persistent storage through MySQL.
 * 
 * @author Inventory Management System
 * @version 1.0
 */
public class DBInventoryManager {
    
    /**
     * Creates the necessary database tables if they don't already exist.
     * <p>
     * This method establishes the database schema by creating the inventory_items table
     * with the appropriate columns and constraints. It uses the CREATE TABLE IF NOT EXISTS
     * statement to ensure the table is only created if it doesn't already exist.
     * <p>
     * Table Schema:
     * <ul>
     *     <li>id - VARCHAR(10) PRIMARY KEY: Unique identifier for each item</li>
     *     <li>name - VARCHAR(100) NOT NULL: Name/description of the item</li>
     *     <li>quantity - INT NOT NULL: Current stock quantity</li>
     *     <li>price - DOUBLE NOT NULL: Unit price of the item</li>
     *     <li>category - VARCHAR(50) NOT NULL: Category classification of the item</li>
     * </ul>
     */
    public void initializeDatabase() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS inventory_items (" +
            "id VARCHAR(10) PRIMARY KEY, " +
            "name VARCHAR(100) NOT NULL, " +
            "quantity INT NOT NULL, " +
            "price DOUBLE NOT NULL, " +
            "category VARCHAR(50) NOT NULL" +
            ")";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Adds sample data to the database if it is empty.
     * <p>
     * This method first checks if the inventory_items table contains any records.
     * If the table is empty, it populates it with sample inventory items to provide
     * initial data for the application.
     * <p>
     * The sample data includes a variety of items across different categories
     * with different quantities and prices to demonstrate the application's functionality.
     */
    public void initializeSampleData() {
        // Check if table is empty
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM inventory_items")) {
            
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Add sample data
                addItem(new InventoryItem("ITM001", "Laptop", 15, 899.99, "Electronics"));
                addItem(new InventoryItem("ITM002", "Desk Chair", 8, 129.99, "Furniture"));
                addItem(new InventoryItem("ITM003", "Wireless Mouse", 25, 24.99, "Electronics"));
                addItem(new InventoryItem("ITM004", "Office Desk", 5, 249.99, "Furniture"));
                addItem(new InventoryItem("ITM005", "Laser Printer", 10, 299.99, "Electronics"));
                System.out.println("Sample data added successfully");
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Adds a new inventory item to the database.
     * <p>
     * This method inserts a new record into the inventory_items table based on the
     * provided InventoryItem object. It uses a prepared statement with parameter
     * binding to safely insert the data and prevent SQL injection.
     * 
     * @param item The InventoryItem object containing the data to be added
     * @return boolean True if the item was successfully added, false otherwise
     */
    public boolean addItem(InventoryItem item) {
        String sql = "INSERT INTO inventory_items (id, name, quantity, price, category) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setString(5, item.getCategory());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates an existing inventory item in the database.
     * <p>
     * This method updates a record in the inventory_items table based on the
     * provided InventoryItem object. It uses a prepared statement with parameter
     * binding to safely update the data and prevent SQL injection.
     * <p>
     * The item is identified by its ID, which is used in the WHERE clause of the
     * UPDATE statement.
     * 
     * @param item The InventoryItem object containing the updated data
     * @return boolean True if the item was successfully updated, false otherwise
     */
    public boolean updateItem(InventoryItem item) {
        String sql = "UPDATE inventory_items SET name = ?, quantity = ?, price = ?, category = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantity());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setString(4, item.getCategory());
            pstmt.setString(5, item.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes an inventory item from the database by its ID.
     * <p>
     * This method removes a record from the inventory_items table based on the
     * provided item ID. It uses a prepared statement with parameter binding to
     * safely execute the deletion and prevent SQL injection.
     * 
     * @param id The ID of the item to delete
     * @return boolean True if the item was successfully deleted, false otherwise
     */
    public boolean deleteItem(String id) {
        String sql = "DELETE FROM inventory_items WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all inventory items from the database.
     * <p>
     * This method executes a SELECT query to retrieve all records from the
     * inventory_items table and converts them into InventoryItem objects.
     * The results are returned as a LinkedList for the application to use.
     * 
     * @return LinkedList<InventoryItem> A list of all inventory items in the database
     */
    public LinkedList<InventoryItem> getAllItems() {
        LinkedList<InventoryItem> items = new LinkedList<>();
        String sql = "SELECT * FROM inventory_items ORDER BY id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving items: " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Searches for a specific inventory item by its ID.
     * <p>
     * This method executes a SELECT query to find a record with the matching ID
     * in the inventory_items table. If found, it converts the database record
     * into an InventoryItem object and returns it.
     * 
     * @param id The ID of the item to search for
     * @return InventoryItem The found item, or null if no item with the given ID exists
     */
    public InventoryItem searchItem(String id) {
        String sql = "SELECT * FROM inventory_items WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractItemFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching for item: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Searches for inventory items by name or ID containing the query string.
     * <p>
     * This method executes a SELECT query with LIKE clauses to find records where
     * either the ID or name contains the provided query string. The search is
     * case-insensitive due to the use of LIKE with % wildcards.
     * 
     * @param query The search string to look for in item IDs or names
     * @return LinkedList<InventoryItem> A list of items matching the search criteria
     */
    public LinkedList<InventoryItem> searchItems(String query) {
        LinkedList<InventoryItem> items = new LinkedList<>();
        String sql = "SELECT * FROM inventory_items WHERE id LIKE ? OR name LIKE ? ORDER BY id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(extractItemFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching for items: " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Helper method to extract an InventoryItem object from a ResultSet row.
     * <p>
     * This method maps the columns from the current row of the ResultSet to
     * properties of a new InventoryItem object. It's used internally by the
     * search and retrieval methods to convert database records to objects.
     * 
     * @param rs The ResultSet containing the data to extract
     * @return InventoryItem A new InventoryItem object populated with data from the ResultSet
     * @throws SQLException If there's an error accessing the ResultSet data
     */
    private InventoryItem extractItemFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        int quantity = rs.getInt("quantity");
        double price = rs.getDouble("price");
        String category = rs.getString("category");
        
        return new InventoryItem(id, name, quantity, price, category);
    }
    
    /**
     * Debug method to check for duplicate IDs in the database.
     * <p>
     * This method executes a query that uses GROUP BY and HAVING clauses to
     * identify any duplicate IDs in the inventory_items table. It's primarily
     * used for debugging and data integrity checks.
     * <p>
     * If duplicates are found, they are reported in the console output.
     */
    public void checkForDuplicates() {
        String sql = "SELECT id, COUNT(*) as count FROM inventory_items GROUP BY id HAVING COUNT(*) > 1";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            boolean duplicatesFound = false;
            
            while (rs.next()) {
                duplicatesFound = true;
                String id = rs.getString("id");
                int count = rs.getInt("count");
                System.out.println("WARNING: Found duplicate ID in database: " + id + " (appears " + count + " times)");
            }
            
            if (!duplicatesFound) {
                System.out.println("No duplicate IDs found in database.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking for duplicates: " + e.getMessage());
        }
    }

    /**
     * Generates a unique ID for a new inventory item.
     * <p>
     * This method queries the database to find the highest existing ID
     * and increments it to create a new unique ID. The format is ITMxxx
     * where xxx is a zero-padded number (e.g., ITM001, ITM002, etc.)
     * 
     * @return String A new unique ID for an inventory item
     */
    public String generateUniqueId() {
        String sql = "SELECT id FROM inventory_items ORDER BY id DESC LIMIT 1";
        String prefix = "ITM";
        int nextNumber = 1;
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastId = rs.getString("id");
                if (lastId != null && lastId.startsWith(prefix)) {
                    try {
                        String numPart = lastId.substring(prefix.length());
                        nextNumber = Integer.parseInt(numPart) + 1;
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.err.println("Error parsing last ID: " + e.getMessage());
                        // If there's an error parsing, just use the default value
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating unique ID: " + e.getMessage());
        }
        
        // Format the ID with leading zeros (ITM001, ITM002, etc.)
        return String.format("%s%03d", prefix, nextNumber);
    }
} 