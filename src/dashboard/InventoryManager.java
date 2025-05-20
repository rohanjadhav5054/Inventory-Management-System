//InventoryManager.java
package dashboard;

import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages inventory items using LinkedList implementation
 */
public class InventoryManager {
    private LinkedList<InventoryItem> inventoryList;
    private Set<String> itemIds;

    public InventoryManager() {
        System.out.println("Creating new InventoryManager");
        inventoryList = new LinkedList<>();
        itemIds = new HashSet<>(); // Start with a fresh set
        initializeSampleData();
        System.out.println("InventoryManager created with " + inventoryList.size() + " items");
    }
    
    private void initializeSampleData() {
        // Clear any existing data
        System.out.println("Initializing sample data - clearing existing data");
        inventoryList.clear();
        itemIds.clear();
        
        // Comment out sample data to prevent accidental duplicates
        // Un-comment for testing if needed
        // 
//         addItem(new InventoryItem("ITM001", "Laptop", 15, 899.99, "Electronics"));
//         addItem(new InventoryItem("ITM002", "Desk Chair", 8, 129.99, "Furniture"));
//         addItem(new InventoryItem("ITM003", "Wireless Mouse", 25, 24.99, "Electronics"));
//         addItem(new InventoryItem("ITM004", "Office Desk", 5, 249.99, "Furniture"));
//         addItem(new InventoryItem("ITM005", "Laser Printer", 10, 299.99, "Electronics"));
    }

    // Add new item - prevent duplicates
    public boolean addItem(InventoryItem item) {
        // Validate item
        if (item == null || item.getId() == null || item.getId().trim().isEmpty()) {
            System.out.println("ERROR: Invalid item or empty ID");
            return false;
        }
        
        // Make sure the ID is trimmed for consistency
        String itemId = item.getId().trim();
        
        // Print current items in inventory for debugging
        System.out.println("Current item IDs before adding: " + itemIds);
        
        // Check if item with this ID already exists
        if (itemIds.contains(itemId)) {
            System.out.println("ERROR: Item with ID " + itemId + " already exists");
            return false;
        }
        
        // Double-check the list to ensure there are no duplicates (belt and suspenders approach)
        for (InventoryItem existingItem : inventoryList) {
            if (existingItem.getId().trim().equals(itemId)) {
                System.out.println("ERROR: Found duplicate ID " + itemId + " in list that wasn't in Set");
                // Add to set to ensure consistency
                itemIds.add(itemId);
                return false;
            }
        }
        
        // Add item and track ID
        inventoryList.add(item);
        itemIds.add(itemId);
        System.out.println("Added item with ID: " + itemId + ", current items count: " + inventoryList.size());
        System.out.println("Current item IDs after adding: " + itemIds);
        
        // Debug - check if there are any duplicates
        checkForDuplicates();
        
        return true;
    }

    // Update existing item
    public boolean updateItem(String id, String name, int quantity, double price, String category) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        id = id.trim();
        
        for (InventoryItem item : inventoryList) {
            if (item.getId().equals(id)) {
                item.setName(name);
                item.setQuantity(quantity);
                item.setPrice(price);
                item.setCategory(category);
                return true;
            }
        }
        return false;
    }
    
    // Update existing item with InventoryItem object
    public boolean updateItem(InventoryItem updatedItem) {
        if (updatedItem == null || updatedItem.getId() == null || updatedItem.getId().trim().isEmpty()) {
            return false;
        }
        
        String id = updatedItem.getId().trim();
        
        // Check if ID exists
        if (!itemIds.contains(id)) {
            return false;
        }
        
        // Find and update item
        for (int i = 0; i < inventoryList.size(); i++) {
            InventoryItem item = inventoryList.get(i);
            if (item.getId().equals(id)) {
                inventoryList.set(i, updatedItem);
                System.out.println("Updated item with ID: " + id + ", current items count: " + inventoryList.size());
                return true;
            }
        }
        
        return false;
    }

    // Delete item
    public boolean deleteItem(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        final String trimmedId = id.trim(); // Make effectively final for lambda
        int initialSize = inventoryList.size();
        boolean removed = inventoryList.removeIf(item -> item.getId().equals(trimmedId));
        
        if (removed) {
            itemIds.remove(trimmedId);
            System.out.println("Deleted item with ID: " + trimmedId + ", items before: " + initialSize + ", after: " + inventoryList.size());
        }
        
        return removed;
    }

    // Get all items
    public LinkedList<InventoryItem> getAllItems() {
        // Return a deep copy to prevent external modification
        LinkedList<InventoryItem> copy = new LinkedList<>();
        
        // Use a Set to track added IDs to ensure no duplicates
        Set<String> addedIds = new HashSet<>();
        
        for (InventoryItem item : inventoryList) {
            if (item != null) {
                String id = item.getId().trim();
                if (!addedIds.contains(id)) {
                    copy.add(item);
                    addedIds.add(id);
                } else {
                    System.out.println("WARNING: Prevented duplicate ID " + id + " in getAllItems()");
                }
            }
        }
        
        System.out.println("Retrieved all items, count: " + copy.size());
        return copy;
    }

    // Search item by ID
    public InventoryItem searchItem(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        
        id = id.trim();
        
        for (InventoryItem item : inventoryList) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
    
    // Search items by name or ID
    public LinkedList<InventoryItem> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new LinkedList<>();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        LinkedList<InventoryItem> results = inventoryList.stream()
                .filter(item -> item.getId().toLowerCase().contains(lowerQuery) || 
                        item.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toCollection(LinkedList::new));
        
        return results;
    }
    
    // Debug method to check for duplicate IDs
    public void checkForDuplicates() {
        Set<String> foundIds = new HashSet<>();
        Set<String> duplicateIds = new HashSet<>();
        
        for (InventoryItem item : inventoryList) {
            String id = item.getId();
            if (!foundIds.add(id)) {
                duplicateIds.add(id);
            }
        }
        
        if (!duplicateIds.isEmpty()) {
            System.out.println("WARNING: Found duplicate IDs: " + duplicateIds);
        } else {
            System.out.println("No duplicate IDs found.");
        }
        
        // Verify items set matches inventory list
        if (itemIds.size() != inventoryList.size()) {
            System.out.println("WARNING: itemIds set size (" + itemIds.size() + 
                    ") does not match inventoryList size (" + inventoryList.size() + ")");
        }
    }
}