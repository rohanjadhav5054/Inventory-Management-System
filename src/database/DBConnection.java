//DBConnection.java
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility Class
 * 
 * This class is responsible for managing database connections to MySQL.
 * It implements a simple connection pool pattern by maintaining a single
 * database connection that can be reused throughout the application.
 * <p>
 * The class provides methods to:
 * <ul>
 *     <li>Establish a connection to the MySQL database</li>
 *     <li>Reuse an existing connection if available</li>
 *     <li>Close the connection when no longer needed</li>
 * </ul>
 * <p>
 * Configuration parameters for the database connection (URL, username, password)
 * are stored as constants in this class and should be modified to match your
 * MySQL server configuration.
 * 
 * @author Inventory Management System
 * @version 1.0
 */
public class DBConnection {
    // Connection URL with parameters to create database if it doesn't exist
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_system?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false";
    
    // Set your actual MySQL credentials here
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Rohan@5054"; 
    
    private static Connection connection = null;
    
    /**
     * Get a connection to the database.
     * <p>
     * This method returns an active connection to the MySQL database. If a connection
     * already exists and is valid, it will be reused. Otherwise, a new connection 
     * will be established.
     * <p>
     * The method includes detailed error handling for common database connection issues:
     * <ul>
     *     <li>MySQL JDBC driver not found</li>
     *     <li>Database server not running</li>
     *     <li>Invalid credentials</li>
     *     <li>Network issues</li>
     * </ul>
     * 
     * @return Connection - An active connection to the MySQL database
     * @throws SQLException if a database connection error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Load the MySQL JDBC driver
                try {
                    System.out.println("Loading MySQL JDBC driver...");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("MySQL JDBC driver loaded successfully");
                } catch (ClassNotFoundException e) {
                    System.err.println("ERROR: MySQL JDBC driver not found!");
                    System.err.println("Make sure mysql-connector-j-8.0.33.jar is in the classpath");
                    throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
                }
                
                // Try to connect to the database
                System.out.println("Connecting to MySQL database...");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection established successfully");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            System.err.println("Please check these items:");
            System.err.println("1. Make sure MySQL is running");
            System.err.println("2. Verify your username and password are correct");
            System.err.println("3. Ensure the inventory_system database exists or you have permissions to create it");
            throw e;
        }
    }
    
    /**
     * Close the database connection.
     * <p>
     * This method safely closes the active database connection if one exists.
     * It should be called when the application is shutting down to properly
     * release database resources.
     * <p>
     * Any errors that occur during connection closure are logged but not thrown.
     */

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
} 