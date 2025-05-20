//DatabaseInitializer.java
package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Database Initialization Utility Class
 * 
 * This class is responsible for initializing the database structure and sample data
 * for the Inventory Management System. It is called at application startup to ensure
 * that all required database tables exist and contain necessary sample data.
 * 
 * The initialization process includes:
 * 1. Creating the inventory database if it doesn't exist
 * 2. Creating required tables (items, users, etc.)
 * 3. Populating tables with sample data for demonstration purposes
 *
 * This class uses DBInventoryManager to perform the actual database operations.
 */
public class DatabaseInitializer {
    /**
     * Initializes the database and tables required for the application.
     * 
     * This method is called during application startup to ensure the database
     * and necessary tables are created and populated with sample data.
     * 
     * The method delegates to DBInventoryManager for the actual database operations:
     * - Creating tables if they don't exist
     * - Adding sample inventory items for demonstration
     * 
     * Any exceptions that occur during initialization are caught, logged, and
     * displayed to the console, but the application will continue to run.
     * 
     * @throws RuntimeException if a critical database error occurs that prevents
     *         the application from functioning properly
     */
    public static void initialize() {
        try {
            System.out.println("Initializing the database and tables...");
            
            // Initialize tables and sample data
            DBInventoryManager dbManager = new DBInventoryManager();
            dbManager.initializeDatabase();
            dbManager.initializeSampleData();
            
            System.out.println("Database initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 