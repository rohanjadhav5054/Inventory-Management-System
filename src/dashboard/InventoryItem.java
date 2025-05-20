package dashboard;

/**
 * Inventory Item Model Class
 *
 * This class represents an inventory item in the system and serves as the core
 * data model for the Inventory Management System. It encapsulates all the
 * properties that define an item in inventory, including its unique identifier,
 * name, quantity, price, and category.
 * <p>
 * The class provides:
 * <ul>
 *     <li>A complete set of getters and setters for all properties</li>
 *     <li>Proper implementation of equals() and hashCode() for collection operations</li>
 *     <li>A formatted toString() method for debugging and display purposes</li>
 * </ul>
 * <p>
 * This class is used throughout the application for data transfer between
 * the UI, business logic, and database layers.
 * 
 * @author Inventory Management System
 * @version 1.0
 */
public class InventoryItem {
    private String id;       // Unique identifier for the item
    private String name;     // Name/description of the item
    private int quantity;    // Current stock quantity
    private double price;    // Unit price of the item
    private String category; // Category classification

    /**
     * Constructs a new InventoryItem with the specified properties.
     * 
     * @param id       The unique identifier for the item
     * @param name     The name/description of the item
     * @param quantity The current stock quantity
     * @param price    The unit price of the item
     * @param category The category classification
     */
    public InventoryItem(String id, String name, int quantity, double price, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    /**
     * Gets the unique identifier of this item.
     * @return The item's ID
     */
    public String getId() { return id; }
    
    /**
     * Gets the name/description of this item.
     * @return The item's name
     */
    public String getName() { return name; }
    
    /**
     * Gets the current stock quantity of this item.
     * @return The quantity in stock
     */
    public int getQuantity() { return quantity; }
    
    /**
     * Gets the unit price of this item.
     * @return The item's price
     */
    public double getPrice() { return price; }
    
    /**
     * Gets the category classification of this item.
     * @return The item's category
     */
    public String getCategory() { return category; }

    /**
     * Sets the unique identifier of this item.
     * @param id The new ID to set
     */
    public void setId(String id) { this.id = id; }
    
    /**
     * Sets the name/description of this item.
     * @param name The new name to set
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Sets the current stock quantity of this item.
     * @param quantity The new quantity to set
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    /**
     * Sets the unit price of this item.
     * @param price The new price to set
     */
    public void setPrice(double price) { this.price = price; }
    
    /**
     * Sets the category classification of this item.
     * @param category The new category to set
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Returns a string representation of this inventory item.
     * <p>
     * The returned string includes all properties of the item formatted
     * for easy readability.
     * 
     * @return A formatted string containing the item's properties
     */
    @Override
    public String toString() {
        return String.format("InventoryItem[id=%s, name=%s, quantity=%d, price=%.2f, category=%s]",
                id, name, quantity, price, category);
    }
    
    /**
     * Checks if this inventory item is equal to another object.
     * <p>
     * Two inventory items are considered equal if they have the same ID.
     * This implementation ignores other properties and only compares IDs,
     * as ID is the unique identifier for inventory items.
     * 
     * @param obj The object to compare with
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        InventoryItem otherItem = (InventoryItem) obj;
        return id.equals(otherItem.id);
    }
    
    /**
     * Returns a hash code value for this inventory item.
     * <p>
     * The hash code is based solely on the item's ID, consistent with
     * the equals method implementation.
     * 
     * @return A hash code value for this item
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}