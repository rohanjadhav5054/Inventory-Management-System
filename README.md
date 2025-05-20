# Inventory Management System

A Java-based desktop application for managing inventory with MySQL database integration for persistent storage.

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [System Architecture](#system-architecture)
4. [Database Setup](#database-setup)
5. [Running the Application](#running-the-application)
6. [Code Structure](#code-structure)
7. [MySQL Integration](#mysql-integration)
8. [User Interface Components](#user-interface-components)
9. [How to Extend](#how-to-extend)

## Overview

The Inventory Management System is a desktop application built with Java Swing that helps businesses track their inventory items. It features a clean, modern UI and uses MySQL for persistent data storage, ensuring that inventory data is preserved between sessions.

## Features

- **User Authentication**: Secure login system
- **Inventory Dashboard**: View all inventory items in a sortable table
- **CRUD Operations**: Add, view, edit, and delete inventory items
- **Filtering & Search**: Filter items by category or search by name/ID
- **Data Visualization**: Dashboard with key metrics (total products, out of stock, etc.)
- **Reports**: Generate stock and inventory value reports
- **History Tracking**: Track operations performed on inventory items
- **Data Persistence**: MySQL database integration for reliable data storage

## System Architecture

The application follows a layered architecture:

1. **Presentation Layer**: Java Swing UI components
2. **Business Logic Layer**: Inventory management logic
3. **Data Access Layer**: Database operations via JDBC
4. **Database Layer**: MySQL database

## Database Setup

### MySQL Setup

1. Install MySQL Server if not already installed:
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Install with default settings

2. Configure MySQL:
   - Start MySQL Server
   - Log in to MySQL: `mysql -u root -p`
   - Create the database using either:
     - Run the included SQL script: `mysql -u root -p < db_schema.sql`
     - Or manually create the database:
       ```sql
       CREATE DATABASE inventory_system;
       USE inventory_system;
       CREATE TABLE inventory_items (
           id VARCHAR(10) PRIMARY KEY,
           name VARCHAR(100) NOT NULL,
           quantity INT NOT NULL,
           price DOUBLE NOT NULL,
           category VARCHAR(50) NOT NULL
       );
       ```

3. Configure Database Connection:
   - Open the file `src/database/DBConnection.java`
   - Update the following constants with your MySQL credentials:
     ```java
     private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_system?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false";
     private static final String DB_USER = "root";
     private static final String DB_PASSWORD = "your_password_here";
     ```

### MySQL JDBC Driver Setup

1. Download MySQL JDBC Driver:
   - Download the MySQL Connector/J JAR file from https://dev.mysql.com/downloads/connector/j/
   - Select "Platform Independent" and download the ZIP archive
   - Extract the JAR file (e.g., `mysql-connector-j-8.0.33.jar`)

2. Add the MySQL JDBC driver to your project:
   - Copy the MySQL connector JAR file to the `lib` folder
   - Add the JAR to your project's classpath

## Running the Application

1. Ensure MySQL server is running
2. Run the `Main.java` file
3. Login with default credentials:
   - Username: `admin`
   - Password: `1234`

## Code Structure

The application is organized into several packages:

### `src/loginpage`
Contains login screen components and authentication logic:
- `LoginPage.java`: Main login screen UI
- `RoundedTextField.java`: Custom rounded text field component
- `RoundedPasswordField.java`: Custom rounded password field component
- `RoundedButton.java`: Custom rounded button component

### `src/dashboard`
Contains the main inventory dashboard UI and logic:
- `InventoryDashboard.java`: Main application UI with table, filters, and buttons
- `InventoryItem.java`: Data model for inventory items
- `InventoryManager.java`: In-memory inventory manager (legacy, used as a fallback)
- `ItemDialog.java`: Dialog for adding/editing items
- `RecentItemsPanel.java`: UI component for recent items

### `src/database`
Contains database connection and data access objects:
- `DBConnection.java`: Manages database connections
- `DBInventoryManager.java`: MySQL implementation of inventory manager
- `DatabaseInitializer.java`: Sets up database schema and sample data

### `src/Main.java`
Application entry point that initializes database and starts the UI.

## MySQL Integration

The system uses JDBC to connect to MySQL. Key components:

### `DBConnection.java`
Manages the database connection pool and provides connection objects to other classes.

```java
public static Connection getConnection() throws SQLException {
    // Load MySQL JDBC driver
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    // Create a connection to the database
    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    return connection;
}
```

### `DBInventoryManager.java`
Handles all database operations for inventory items:

- `initializeDatabase()`: Creates necessary tables
- `addItem()`: Inserts new items
- `updateItem()`: Updates existing items
- `deleteItem()`: Removes items
- `getAllItems()`: Retrieves all items
- `searchItem()`: Finds items by ID
- `searchItems()`: Searches items by name or ID

## User Interface Components

### Login Screen
- Username and password fields with custom rounded styling
- Error handling for invalid credentials

### Inventory Dashboard
- **Summary Cards**: Shows total products, out-of-stock items, categories, and total value
- **Data Table**: Displays all inventory items with sortable columns
- **Search & Filter**: Search by name/ID and filter by category
- **Operation Buttons**: Add, Edit, Delete, and Refresh data

### Dialogs
- **Add/Edit Dialog**: Form for adding or editing inventory items
- **Reports**: Stock report and value report dialogs

## How to Extend

### Adding New Features
1. **New Item Attributes**:
   - Add fields to the `InventoryItem` class
   - Update the database schema in `DBInventoryManager.initializeDatabase()`
   - Modify the ItemDialog to include the new fields

2. **New Reports**:
   - Add a new method in `InventoryDashboard` similar to `generateStockReport()`
   - Create a menu item to trigger the report

3. **User Management**:
   - Create a new package for user management
   - Add a users table to the database
   - Implement user CRUD operations

### Customizing the UI
- UI components use standard Java Swing with custom styling
- Most visual elements are created in the constructor or separate methods
- Modify colors, fonts, and layouts in the respective component classes 