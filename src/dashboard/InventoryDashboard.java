package dashboard;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import database.DBInventoryManager;
import java.util.Collections;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Main Dashboard for the Inventory Management System
 * 
 * This class provides the primary user interface for the application, offering
 * a comprehensive dashboard to manage inventory items. It serves as the central
 * control panel where users can view, add, edit, and delete inventory items, as
 * well as generate reports and track operation history.
 * 
 * Key Features:
 * - Interactive data table with sorting and filtering capabilities
 * - Summary cards showing key inventory metrics
 * - CRUD operations for inventory items (Add, Edit, Delete)
 * - Search functionality by name or ID
 * - Category filtering
 * - Report generation (Stock Report, Value Report)
 * - Operation history tracking and export
 * - Menu-based navigation
 * 
 * Dashboard Components:
 * 1. Summary Panel - Shows total products, out-of-stock items, categories, and total value
 * 2. Inventory Table - Displays all inventory items with sortable columns
 * 3. Filter Panel - Allows searching and filtering by category
 * 4. Action Buttons - Quick access to common operations
 * 5. Menu Bar - Access to all features including reports and history
 * 
 * The dashboard uses a database-backed inventory manager (DBInventoryManager) to
 * perform all operations on the inventory data, ensuring data persistence across
 * application sessions.
 *
 */
public class InventoryDashboard extends JFrame {
    private final DBInventoryManager inventoryManager;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JLabel totalProductsLabel, outOfStockLabel, categoriesLabel, totalValueLabel;
    private JComboBox<String> categoryFilter;
    private JPanel buttonPanel;
    private JTextField searchField;
    private Set<String> uniqueCategories = new HashSet<>(); // To track unique categories
    
    // History tracking
    private ArrayList<OperationRecord> operationHistory = new ArrayList<>();
    private JMenu historyMenu;
    private static final int MAX_MENU_HISTORY_ITEMS = 10; // Only limit menu items, not the actual history
    private JFrame historyFrame; // Reference to the history frame
    private DefaultTableModel historyTableModel; // Reference to the history table model

    public InventoryDashboard() {
        // Create a fresh inventory manager using the database
        inventoryManager = new DBInventoryManager();
        System.out.println("\n===== INITIALIZING INVENTORY DASHBOARD =====");
        System.out.println("InventoryDashboard constructor called");
        
        // Initialize components first
        initializeUI();
        createMenuBar();
        createMainPanel();  // This creates the summary panel and initializes the labels
        
        // Then load data ONCE
        System.out.println("Initial table refresh");
        refreshInventoryTable();
        updateSummaryData();
        System.out.println("===== INITIALIZATION COMPLETE =====\n");
    }

    private void initializeUI() {
        setTitle("Inventory Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Try to load icon, but don't crash if it fails
        try {
            setIconImage(new ImageIcon(getClass().getResource("/resources/images/icons/logo.png")).getImage());
        } catch (Exception e) {
            System.out.println("Logo image not found: " + e.getMessage());
            // Continue without setting the icon
        }
        
        // Set modern look and feel for entire application
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("ProgressBar.arc", 15);
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Modern color scheme
        Color primaryColor = new Color(25, 118, 210);  // Material Blue
        Color accentColor = new Color(255, 152, 0);    // Material Orange
        Color backgroundColor = new Color(245, 245, 245); // Light gray background
        
        getContentPane().setBackground(backgroundColor);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(25, 118, 210)); // Material Blue
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Common menu item style
        Color menuForeground = Color.WHITE;
        Font menuFont = new Font("Segoe UI", Font.BOLD, 13);

        // File Menu
        JMenu fileMenu = createStyledMenu("File", menuForeground, menuFont);
        JMenuItem exportItem = createStyledMenuItem("Export Data", menuFont, e -> exportData());
        JMenuItem exitItem = createStyledMenuItem("Exit", menuFont, e -> System.exit(0));

        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Operations Menu
        JMenu operationsMenu = createStyledMenu("Operations", menuForeground, menuFont);
        JMenuItem addItem = createStyledMenuItem("Add Item", menuFont, e -> showAddItemDialog());
        JMenuItem updateItem = createStyledMenuItem("Edit Item", menuFont, e -> showEditItemDialog());
        JMenuItem deleteItem = createStyledMenuItem("Delete Item", menuFont, e -> deleteItem());
        JMenuItem refreshItem = createStyledMenuItem("Refresh Data", menuFont, e -> refreshInventoryTable());

        operationsMenu.add(addItem);
        operationsMenu.add(updateItem);
        operationsMenu.add(deleteItem);
        operationsMenu.addSeparator();
        operationsMenu.add(refreshItem);

        // Reports Menu
        JMenu reportsMenu = createStyledMenu("Reports", menuForeground, menuFont);
        JMenuItem stockReport = createStyledMenuItem("Stock Report", menuFont, e -> generateStockReport());
        JMenuItem valueReport = createStyledMenuItem("Inventory Value", menuFont, e -> generateValueReport());

        reportsMenu.add(stockReport);
        reportsMenu.add(valueReport);
        
        // History Menu
        historyMenu = createStyledMenu("History", menuForeground, menuFont);
        JMenuItem viewAllHistoryItem = createStyledMenuItem("View Recent History", menuFont, e -> showAllHistory());
        JMenuItem clearHistoryItem = createStyledMenuItem("Clear History", menuFont, e -> clearHistory());
        historyMenu.add(viewAllHistoryItem);
        historyMenu.addSeparator();
        historyMenu.add(clearHistoryItem);

        menuBar.add(fileMenu);
        menuBar.add(operationsMenu);
        menuBar.add(reportsMenu);
        menuBar.add(historyMenu);
        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text, Color foreground, Font font) {
        JMenu menu = new JMenu(text);
        menu.setForeground(foreground);
        menu.setFont(font);
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text, Font font, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(font);
        menuItem.addActionListener(action);
        return menuItem;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Top Panel - Summary Cards
        JPanel summaryPanel = createSummaryPanel();
        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Center Panel - Table only (removed Recent Items)
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        return mainPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.setOpaque(false);

        // Create and add all summary cards with modern colors
        panel.add(createSummaryCard("Total Products", "0", new Color(33, 150, 243), "\uf07a")); // Shopping cart icon
        panel.add(createSummaryCard("Out of Stock", "0", new Color(244, 67, 54), "\uf06a")); // Exclamation icon
        panel.add(createSummaryCard("Categories", "0", new Color(76, 175, 80), "\uf0ca")); // List icon
        panel.add(createSummaryCard("Total Value", "Rs 0.00", new Color(156, 39, 176), "\uf157")); // Rupee icon

        return panel;
    }

    private JPanel createSummaryCard(String title, String value, Color color, String iconCode) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        
        // Create a gradient panel for the card header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient from color to slightly darker
                GradientPaint gp = new GradientPaint(
                    0, 0, color, 
                    0, getHeight(), color.darker()
                );
                
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 5));
        
        // Create a rounded border with shadow effect
        card.setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 15, 15);
                
                // Draw border
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 15, 15);
                
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(20, 25, 20, 25);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });

        // Create title with icon using a custom font
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);
        
        // Label for title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        // Icon using Unicode character (would normally use a proper icon font)
        JLabel iconLabel = new JLabel();
        iconLabel.setForeground(color);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(30, 30));
        
        titlePanel.add(iconLabel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Value label with special styling
        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        // Store the reference to update later
        switch(title) {
            case "Total Products": totalProductsLabel = valueLabel; break;
            case "Out of Stock": outOfStockLabel = valueLabel; break;
            case "Categories": categoriesLabel = valueLabel; break;
            case "Total Value": totalValueLabel = valueLabel; break;
        }

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(titlePanel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFormPanel() {
        // This method is no longer needed since we're using dialogs
        /*
        return new JPanel(); // Empty panel
        */
        return null;
    }

    private JTextField createStyledTextField() {
        // This method is no longer needed since we're using dialogs
        /*
        return new JTextField();
        */
        return null;
    }

    private JPanel createFormGroup(String label, JComponent field) {
        // This method is no longer needed since we're using dialogs
        /*
        return new JPanel();
        */
        return null;
    }

    private JPanel createTablePanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Table title
        JLabel tableTitle = new JLabel("Inventory Items");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(25, 118, 210));
        
        // Filter Panel with search
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        searchField = new JTextField(15);
        searchField.setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRoundRect(x, y, width-1, height-1, 15, 15);
                
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(8, 10, 8, 10);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        searchField.setToolTipText("Search by item name or ID");
        
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchButton.setBackground(new Color(220, 53, 69)); // Red color
        searchButton.setForeground(Color.RED);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(200, 35, 51));
                g2d.drawRoundRect(x, y, width-1, height-1, 15, 15);
                
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(8, 15, 8, 15);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> performSearch());
        
        // Add hover effect to search button
        searchButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                searchButton.setBackground(new Color(200, 35, 51)); // Darker red on hover
            }
            public void mouseExited(MouseEvent e) {
                searchButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        // Create a separate panel for the search button to ensure it's displayed properly
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(searchButton, BorderLayout.CENTER);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        
        // Category filter
        JPanel categoryPanel = new JPanel(new BorderLayout(5, 0));
        categoryPanel.setOpaque(false);
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        categoryFilter = new JComboBox<>(new DefaultComboBoxModel<String>() {
            private final Set<String> items = new HashSet<>();
            
            @Override
            public void addElement(String item) {
                if (items.add(item)) {
                    super.addElement(item);
                }
            }
            
            @Override
            public void insertElementAt(String item, int index) {
                if (items.add(item)) {
                    super.insertElementAt(item, index);
                }
            }
            
            @Override
            public void removeElement(Object obj) {
                if (obj instanceof String) {
                    items.remove(obj);
                }
                super.removeElement(obj);
            }
            
            @Override
            public void removeAllElements() {
                items.clear();
                super.removeAllElements();
            }
        });
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        categoryFilter.addItem("All Categories");
        categoryFilter.addActionListener(e -> filterTable());
        categoryFilter.setToolTipText("Filter items by category");
        
        // Set maximum row count to enable scrolling in dropdown
        categoryFilter.setMaximumRowCount(5);
        
        categoryPanel.add(categoryLabel, BorderLayout.WEST);
        categoryPanel.add(categoryFilter, BorderLayout.CENTER);
        
        filterPanel.add(searchPanel);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(categoryPanel);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(tableTitle, BorderLayout.NORTH);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Quantity", "Price", "Category", "Value"};
        tableModel = new DefaultTableModel(columns, 0) {
            private Set<String> tableIds = new HashSet<>();
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 2: return Integer.class;  // Quantity
                    case 3: case 5: return Double.class;  // Price and Value
                    default: return String.class;
                }
            }
            
            @Override
            public void setRowCount(int rowCount) {
                super.setRowCount(rowCount);
                // Reset IDs when clearing the table
                if (rowCount == 0) {
                    tableIds.clear();
                }
            }
            
            @Override
            public void addRow(Object[] rowData) {
                // Check for duplicates by ID (column 0)
                if (rowData != null && rowData.length > 0 && rowData[0] != null) {
                    String id = rowData[0].toString().trim();
                    if (tableIds.contains(id)) {
                        System.out.println("WARNING: Prevented adding duplicate ID to table: " + id);
                        return;
                    }
                    tableIds.add(id);
                }
                super.addRow(rowData);
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        inventoryTable.setRowHeight(30);
        inventoryTable.setShowGrid(false);
        inventoryTable.setIntercellSpacing(new Dimension(0, 0));
        
        // Alternating row colors
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
        
        // Custom header renderer
        inventoryTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(25, 118, 210));
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        });
        
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        inventoryTable.setRowSorter(sorter);
        
        // Set column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Price
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Category
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Value

        // Add tooltips
        inventoryTable.setToolTipText("Click column headers to sort. Click on a row to edit item details.");

        // Add selection listener
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            // We no longer need to update text fields on selection
            // since we're using dialogs for editing
        });

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        outerPanel.add(panel, BorderLayout.CENTER);

        return outerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setOpaque(false);
        
        // Store reference to button panel
        this.buttonPanel = panel;

        // Create buttons for operations with direct ActionListener references for debugging
        JButton addBtn = new JButton("Add Item");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBackground(new Color(25, 135, 84));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Add button clicked");
                showAddItemDialog();
            }
        });
        
        JButton updateBtn = new JButton("Edit Item");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        updateBtn.setBackground(new Color(255, 152, 0));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Edit button clicked");
                showEditItemDialog();
            }
        });
        
        JButton deleteBtn = new JButton("Delete Item");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Delete button clicked");
                deleteItem();
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(new Color(13, 110, 253));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refresh button clicked");
                refreshInventoryTable();
            }
        });

        panel.add(refreshBtn);
        panel.add(deleteBtn);
        panel.add(updateBtn);
        panel.add(addBtn);

        return panel;
    }

    // CRUD Operations
    private void showAddItemDialog() {
        System.out.println("Opening Add Item Dialog");
        
        ItemDialog dialog = new ItemDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            System.out.println("Dialog confirmed, getting new item");
            InventoryItem newItem = dialog.getItem();
            System.out.println("New item created: " + newItem);
            
            if (newItem == null) {
                System.out.println("ERROR: Item creation failed");
                showMessage("Failed to create item", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Generate a unique ID for the new item
            String newId = inventoryManager.generateUniqueId();
            System.out.println("Generated ID for new item: " + newId);
            newItem.setId(newId);
            
            System.out.println("Attempting to add item to inventory manager");
            if (inventoryManager.addItem(newItem)) {
                System.out.println("Item added successfully to inventory manager");
                refreshInventoryTable();
                showMessage("Item added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                addToHistory("Added", newItem);
            } else {
                System.out.println("Failed to add item to inventory manager");
                showMessage("Failed to add item. ID may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Dialog cancelled");
        }
    }
    
    private void showEditItemDialog() {
        System.out.println("Opening Edit Item Dialog");
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            System.out.println("No row selected");
            showMessage("Please select an item to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        row = inventoryTable.convertRowIndexToModel(row);
        String id = tableModel.getValueAt(row, 0).toString();
        System.out.println("Selected item ID: " + id);
        InventoryItem item = inventoryManager.searchItem(id);
        
        if (item != null) {
            System.out.println("Found item: " + item);
            ItemDialog dialog = new ItemDialog(this, item);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                System.out.println("Dialog confirmed, getting updated item");
                InventoryItem updatedItem = dialog.getItem();
                System.out.println("Updated item: " + updatedItem);
                if (inventoryManager.updateItem(updatedItem)) {
                    System.out.println("Item updated successfully");
                    refreshInventoryTable();
                    showMessage("Item updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addToHistory("Updated", updatedItem);
                } else {
                    System.out.println("Failed to update item");
                    showMessage("Failed to update item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.out.println("Dialog cancelled");
            }
        } else {
            System.out.println("Item not found");
            showMessage("Item not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem() {
        System.out.println("Deleting item");
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            System.out.println("No row selected");
            showMessage("Please select an item to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        row = inventoryTable.convertRowIndexToModel(row);
        String id = tableModel.getValueAt(row, 0).toString();
        InventoryItem item = inventoryManager.searchItem(id);
        System.out.println("Selected item ID for deletion: " + id);
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Delete confirmed");
            if (inventoryManager.deleteItem(id)) {
                System.out.println("Item deleted successfully");
                refreshInventoryTable();
                showMessage("Item deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (item != null) {
                    addToHistory("Deleted", item);
                }
            } else {
                System.out.println("Failed to delete item");
                showMessage("Failed to delete item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Delete cancelled");
        }
    }

    // Data Management
    private void refreshInventoryTable() {
        // First grab the console with debug info to understand what's happening
        System.out.println("\n===== REFRESHING TABLE =====");
        System.out.println("Stack trace for debugging:");
        Thread.dumpStack();
        
        // Check for duplicates in database
        inventoryManager.checkForDuplicates();
        
        // Clear existing data
        tableModel.setRowCount(0);
        System.out.println("Cleared table, row count: " + tableModel.getRowCount());
        
        // Clear and update unique categories
        uniqueCategories.clear();
        
        // Create a direct Set for tracking IDs - don't copy the LinkedList
        Set<String> addedIds = new HashSet<>();
        
        // Directly iterate through the inventory manager items
        // This ensures we're getting the most up-to-date list
        for (InventoryItem item : inventoryManager.getAllItems()) {
            if (item == null) {
                System.out.println("WARNING: Skipping null item");
                continue;
            }
            
            String itemId = item.getId().trim();
            if (addedIds.contains(itemId)) {
                System.out.println("WARNING: Skipping duplicate item ID in table refresh: " + itemId);
                continue;
            }
            
            // Add to categories
            uniqueCategories.add(item.getCategory());
            
            // Add to table
            System.out.println("Adding to table: " + item);
            double value = item.getQuantity() * item.getPrice();
            tableModel.addRow(new Object[]{
                    itemId,
                    item.getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getCategory(),
                    value
            });
            
            addedIds.add(itemId);
        }
        
        // Update category filter without triggering change events
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        // Remove the action listener temporarily
        ActionListener[] listeners = categoryFilter.getActionListeners();
        for (ActionListener listener : listeners) {
            categoryFilter.removeActionListener(listener);
        }
        
        categoryFilter.removeAllItems();
        categoryFilter.addItem("All Categories");
        
        // Add categories in a deterministic order
        ArrayList<String> sortedCategories = new ArrayList<>(uniqueCategories);
        Collections.sort(sortedCategories);
        for (String category : sortedCategories) {
            categoryFilter.addItem(category);
        }
        
        // Restore the selected category if possible
        if (selectedCategory != null) {
            categoryFilter.setSelectedItem(selectedCategory);
            if (!selectedCategory.equals(categoryFilter.getSelectedItem())) {
                categoryFilter.setSelectedIndex(0); // Default to "All Categories"
            }
        }
        
        // Restore the action listeners
        for (ActionListener listener : listeners) {
            categoryFilter.addActionListener(listener);
        }
        
        System.out.println("Table refreshed with " + tableModel.getRowCount() + " rows");
        System.out.println("Added IDs: " + addedIds);
        System.out.println("===== TABLE REFRESH COMPLETE =====\n");

        // Format currency columns
        TableColumn priceColumn = inventoryTable.getColumnModel().getColumn(3);
        TableColumn valueColumn = inventoryTable.getColumnModel().getColumn(5);
        TableColumn quantityColumn = inventoryTable.getColumnModel().getColumn(2); // Get Quantity column
        
        // Set center alignment for quantity column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        quantityColumn.setCellRenderer(centerRenderer);
        
        priceColumn.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = String.format("Rs %.2f", (Double) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        
        valueColumn.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = String.format("Rs %.2f", (Double) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Update summary data
        updateSummaryData();
    }

    private void filterTable() {
        System.out.println("Filtering table by category");
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        // Store current category to detect if it actually changed
        if (selectedCategory == null) {
            selectedCategory = "All Categories";
        }
        
        // Make a final copy for use in lambda
        final String finalSelectedCategory = selectedCategory;
        
        // Only refresh if "All Categories" is selected
        if ("All Categories".equals(finalSelectedCategory)) {
            System.out.println("Selected 'All Categories', performing full refresh");
            refreshInventoryTable();
            return;
        }

        System.out.println("Filtering by category: " + finalSelectedCategory);
        tableModel.setRowCount(0);
        // Use a Set to prevent duplicates
        Set<String> addedIds = new HashSet<>();
        
        inventoryManager.getAllItems().stream()
                .filter(item -> item.getCategory().equals(finalSelectedCategory))
                .forEach(item -> {
                    String itemId = item.getId().trim();
                    if (!addedIds.contains(itemId)) {
                        double value = item.getQuantity() * item.getPrice();
                        tableModel.addRow(new Object[]{
                                itemId,
                                item.getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getCategory(),
                                value
                        });
                        addedIds.add(itemId);
                    }
                });
        System.out.println("Filtered table to " + tableModel.getRowCount() + " rows");
    }

    private void updateSummaryData() {
        LinkedList<InventoryItem> items = inventoryManager.getAllItems();

        // Total products
        totalProductsLabel.setText(String.valueOf(items.size()));

        // Out of stock
        long outOfStock = items.stream().filter(item -> item.getQuantity() <= 0).count();
        outOfStockLabel.setText(String.valueOf(outOfStock));

        // Categories - use the unique categories set
        categoriesLabel.setText(String.valueOf(uniqueCategories.size()));

        // Total inventory value
        double totalValue = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        totalValueLabel.setText(String.format("Rs. %.2f", totalValue));
    }

    // Utility Methods
    private void clearFields() {
        searchField.setText("");
        inventoryTable.clearSelection();
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Additional Features
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Inventory Data");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implement export logic (CSV, Excel, etc.)
            showMessage("Export functionality coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generateStockReport() {
        StringBuilder report = new StringBuilder("INVENTORY STOCK REPORT\n");
        report.append("========================================\n\n");
        
        report.append(String.format("%-10s %-25s %-15s %-15s\n", "ID", "PRODUCT", "QUANTITY", "STATUS"));
        report.append("---------------------------------------------------------------------\n");
        
        inventoryManager.getAllItems().forEach(item -> {
            String status = item.getQuantity() <= 0 ? "OUT OF STOCK" : 
                          (item.getQuantity() < 5 ? "LOW STOCK" : "IN STOCK");
            report.append(String.format("%-10s %-25s %-15d %-15s\n",
                    item.getId(), item.getName(), item.getQuantity(), status));
        });
        
        report.append("\n\nReport generated on: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        // Show the report in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "Stock Report", JOptionPane.PLAIN_MESSAGE);
        
        // Ask if the user wants to save the report
        int saveOption = JOptionPane.showConfirmDialog(this, 
                "Would you like to save this report to a file?", 
                "Save Report", 
                JOptionPane.YES_NO_OPTION);
        
        if (saveOption == JOptionPane.YES_OPTION) {
            saveReportToFile(report.toString(), "StockReport");
        }
    }

    private void generateValueReport() {
        StringBuilder report = new StringBuilder("INVENTORY VALUE REPORT\n");
        report.append("========================================\n\n");
        
        report.append(String.format("%-10s %-25s %-15s %-15s %-15s\n", 
                "ID", "PRODUCT", "QUANTITY", "UNIT PRICE", "TOTAL VALUE"));
        report.append("---------------------------------------------------------------------------------\n");
        
        double totalValue = 0;
        
        for (InventoryItem item : inventoryManager.getAllItems()) {
            double value = item.getQuantity() * item.getPrice();
            totalValue += value;
            
            report.append(String.format("%-10s %-25s %-15d Rs %-11.2f Rs %-11.2f\n",
                    item.getId(), item.getName(), item.getQuantity(), item.getPrice(), value));
        }
        
        report.append("---------------------------------------------------------------------------------\n");
        report.append(String.format("%67s Rs %-11.2f\n", "TOTAL INVENTORY VALUE:", totalValue));
        
        report.append("\n\nReport generated on: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        // Show the report in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "Value Report", JOptionPane.PLAIN_MESSAGE);
        
        // Ask if the user wants to save the report
        int saveOption = JOptionPane.showConfirmDialog(this, 
                "Would you like to save this report to a file?", 
                "Save Report", 
                JOptionPane.YES_NO_OPTION);
        
        if (saveOption == JOptionPane.YES_OPTION) {
            saveReportToFile(report.toString(), "ValueReport");
        }
    }
    
    /**
     * Helper method to save a report to a file
     * @param reportContent The content of the report to save
     * @param defaultFileName The default file name to suggest
     */
    private void saveReportToFile(String reportContent, String defaultFileName) {
        // Create a timestamp for the filename
        String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String suggestedFileName = defaultFileName + "_" + timestamp + ".txt";
        
        // Create and configure file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new File(suggestedFileName));
        
        // Set up file filter for text files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .txt extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                writer.print(reportContent);
                showMessage("Report saved successfully to: " + fileToSave.getAbsolutePath(), 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                showMessage("Failed to save report: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Helper method to show/hide form and related buttons
    private void showItemForm(boolean show, boolean isNewItem) {
        // This method is no longer needed since we're using dialogs
        // for adding and editing items
        /*
        */
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        if (searchTerm.isEmpty()) {
            System.out.println("Search term is empty, performing full refresh");
            refreshInventoryTable();
            return;
        }
        
        System.out.println("Searching for: " + searchTerm);
        tableModel.setRowCount(0);
        // Use a Set to prevent duplicates
        Set<String> addedIds = new HashSet<>();
        
        inventoryManager.getAllItems().stream()
            .filter(item -> 
                item.getName().toLowerCase().contains(searchTerm) || 
                item.getId().toLowerCase().contains(searchTerm))
            .forEach(item -> {
                String itemId = item.getId().trim();
                if (!addedIds.contains(itemId)) {
                    double value = item.getQuantity() * item.getPrice();
                    tableModel.addRow(new Object[]{
                        itemId,
                        item.getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getCategory(),
                        value
                    });
                    addedIds.add(itemId);
                }
            });
        System.out.println("Search results: " + tableModel.getRowCount() + " items found");
    }

    // History tracking methods
    private void addToHistory(String operation, InventoryItem item) {
        System.out.println("Adding to history: " + operation + " " + item.getId());
        OperationRecord record = new OperationRecord(operation, item);
        operationHistory.add(0, record); // Add to the beginning of the list
        
        // No longer limiting the history size - keep all operations
        
        updateHistoryMenu();
        // Only refresh the history table if it's open, don't refresh the inventory table
        if (historyFrame != null && historyFrame.isVisible() && historyTableModel != null) {
            refreshHistoryTable();
        }
    }
    
    private void updateHistoryMenu() {
        // Clear existing items, except the first "View Recent History" and the last "Clear History" item
        while (historyMenu.getItemCount() > 3) {
            historyMenu.remove(1);
        }
        
        // Add history items - only show the most recent MAX_MENU_HISTORY_ITEMS in the menu
        int itemsToShow = Math.min(operationHistory.size(), MAX_MENU_HISTORY_ITEMS);
        for (int i = 0; i < itemsToShow; i++) {
            final OperationRecord record = operationHistory.get(i);
            JMenuItem historyItem = new JMenuItem(record.toString());
            historyItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Highlight recent operations with different colors
            if (i == 0) {
                historyItem.setForeground(new Color(25, 135, 84)); // Green for most recent
            } else if (i < 3) {
                historyItem.setForeground(new Color(13, 110, 253)); // Blue for recent 
            }
            
            historyItem.addActionListener(e -> showHistoryDetails(record));
            historyMenu.insert(historyItem, i + 1);
        }
        
        // If there are more items than shown in the menu, add a "..." item that opens the full history
        if (operationHistory.size() > MAX_MENU_HISTORY_ITEMS) {
            JMenuItem moreItem = new JMenuItem("... View All History (" + operationHistory.size() + " items)");
            moreItem.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            moreItem.addActionListener(e -> showAllHistory());
            historyMenu.insert(moreItem, MAX_MENU_HISTORY_ITEMS + 1);
        }
    }
    
    private void showHistoryDetails(OperationRecord record) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title with operation and timestamp
        JLabel titleLabel = new JLabel(record.operation + " Operation - " + record.timestamp);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Details table
        String[] columns = {"Property", "Value"};
        Object[][] data = {
                {"ID", record.item.getId()},
                {"Name", record.item.getName()},
                {"Quantity", record.item.getQuantity()},
                {"Price", String.format("Rs %.2f", record.item.getPrice())},
                {"Category", record.item.getCategory()},
                {"Total Value", String.format("Rs %.2f", record.item.getQuantity() * record.item.getPrice())}
        };
        
        JTable detailsTable = new JTable(data, columns);
        detailsTable.setEnabled(false);
        detailsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setPreferredSize(new Dimension(400, 175));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, panel, "Operation Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all operation history?",
                "Confirm Clear History",
                JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            operationHistory.clear();
            updateHistoryMenu();
            
            // Close history frame if it's open
            if (historyFrame != null && historyFrame.isVisible()) {
                historyFrame.dispose();
                historyFrame = null;
                historyTableModel = null;
            }
            
            showMessage("Operation history cleared", "History", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showAllHistory() {
        if (operationHistory.isEmpty()) {
            showMessage("No operations have been performed yet.", "History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // If frame already exists, just bring it to front
        if (historyFrame != null && historyFrame.isVisible()) {
            historyFrame.toFront();
            return;
        }
        
        // Create a new frame to display history
        historyFrame = new JFrame("Operation History (" + operationHistory.size() + " items)");
        historyFrame.setSize(800, 500); // Made slightly taller to show more items
        historyFrame.setLocationRelativeTo(this);
        
        // Create table model for the history
        String[] columns = {"Timestamp", "Operation", "Item ID", "Item Name", "Category", "Quantity", "Price"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add history records to the table
        refreshHistoryTable();
        
        // Create table and add to scroll pane
        JTable historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Set column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Timestamp
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Operation
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Item ID
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Item Name
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(historyTableModel);
        historyTable.setRowSorter(sorter);
        
        // Create a toolbar with options for the history
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton exportButton = new JButton("Export History");
        exportButton.addActionListener(e -> exportHistoryToFile());
        
        JLabel countLabel = new JLabel("Total Operations: " + operationHistory.size());
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        toolbarPanel.add(exportButton);
        toolbarPanel.add(countLabel);
        
        // Add components to frame
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        
        historyFrame.add(mainPanel);
        
        // Add window listener to handle closing
        historyFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                historyFrame = null;
                historyTableModel = null;
            }
        });
        
        // Show the frame
        historyFrame.setVisible(true);
    }
    
    /**
     * Export the complete operation history to a text file
     */
    private void exportHistoryToFile() {
        if (operationHistory.isEmpty()) {
            showMessage("No history to export.", "Export History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder content = new StringBuilder("INVENTORY OPERATION HISTORY\n");
        content.append("============================\n\n");
        content.append(String.format("%-25s %-15s %-10s %-25s %-15s %-10s %-15s\n", 
                "TIMESTAMP", "OPERATION", "ITEM ID", "ITEM NAME", "CATEGORY", "QUANTITY", "PRICE"));
        content.append("------------------------------------------------------------------------------------------------\n");
        
        for (OperationRecord record : operationHistory) {
            content.append(String.format("%-25s %-15s %-10s %-25s %-15s %-10d Rs %-10.2f\n",
                    record.timestamp,
                    record.operation,
                    record.item.getId(),
                    record.item.getName(),
                    record.item.getCategory(),
                    record.item.getQuantity(),
                    record.item.getPrice()));
        }
        
        content.append("\n\nExported on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        content.append("\nTotal Operations: " + operationHistory.size());
        
        // Use the existing report saving functionality
        saveReportToFile(content.toString(), "OperationHistory");
    }
    
    // Refresh the history table if it's open
    private void refreshHistoryTable() {
        if (historyTableModel != null) {
            historyTableModel.setRowCount(0);
            
            // Add history records to the table
            for (OperationRecord record : operationHistory) {
                Object[] row = {
                    record.timestamp,
                    record.operation,
                    record.item.getId(),
                    record.item.getName(),
                    record.item.getCategory(),
                    record.item.getQuantity(),
                    String.format("Rs %.2f", record.item.getPrice())
                };
                historyTableModel.addRow(row);
            }
        }
    }

    // Helper class to track operations
    private class OperationRecord {
        String operation;
        InventoryItem item;
        String timestamp;
        
        public OperationRecord(String operation, InventoryItem item) {
            this.operation = operation;
            this.item = item;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        @Override
        public String toString() {
            return String.format("%s - %s (%s)", timestamp, operation, item.getName());
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                new InventoryDashboard().setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
}