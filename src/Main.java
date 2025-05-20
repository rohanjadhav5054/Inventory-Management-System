/**
 * Main entry point for the Inventory Management System application.
 * This class initializes the database and launches the login page.
 * 
 * The application follows a layered architecture:
 * - UI Layer: LoginPage and InventoryDashboard with supporting UI components
 * - Service Layer: InventoryManager and database management classes
 * - Data Access Layer: DBConnection and DBInventoryManager for database operations
 *
 *
 */
import database.DatabaseInitializer;
import loginpage.LoginPage;

public class Main {
    /**
     * Main method that serves as the entry point for the application.
     * Initializes the database and starts the login page.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Initialize database
            System.out.println("Initializing database...");
            DatabaseInitializer.initialize();
            
            // Start the application
            System.out.println("Starting application...");
        new LoginPage();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
