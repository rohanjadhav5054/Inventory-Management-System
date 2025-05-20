-- Create the database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS inventory_system;

-- Use the database
USE inventory_system;

-- Create inventory_items table
CREATE TABLE IF NOT EXISTS inventory_items (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    category VARCHAR(50) NOT NULL
);

-- Sample data (optional - uncomment to add sample data)
/*
INSERT INTO inventory_items (id, name, quantity, price, category) VALUES
('ITM001', 'Laptop', 15, 899.99, 'Electronics'),
('ITM002', 'Desk Chair', 8, 129.99, 'Furniture'),
('ITM003', 'Wireless Mouse', 25, 24.99, 'Electronics'),
('ITM004', 'Office Desk', 5, 249.99, 'Furniture'),
('ITM005', 'Laser Printer', 10, 299.99, 'Electronics');
*/ 