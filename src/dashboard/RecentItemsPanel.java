//RecentItemsPanel.java
package dashboard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * Panel to display recently added or modified inventory items
 */
public class RecentItemsPanel extends JPanel {
    private LinkedList<InventoryItem> recentItems;
    private JPanel itemsContainer;
    private static final int MAX_ITEMS = 5;

    public RecentItemsPanel() {
        recentItems = new LinkedList<>();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(Color.WHITE);

        // Title panel
        JLabel titleLabel = new JLabel("Recently Modified Items");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Items container with vertical layout
        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);

        // Empty state message
        JLabel emptyLabel = new JLabel("No recent items", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyLabel.setForeground(new Color(150, 150, 150));
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemsContainer.add(emptyLabel);

        JScrollPane scrollPane = new JScrollPane(itemsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addRecentItem(InventoryItem item) {
        // Check if item already exists in recent items
        recentItems.removeIf(i -> i.getId().equals(item.getId()));
        
        // Add to the beginning of the list
        recentItems.addFirst(item);
        
        // Keep only the most recent MAX_ITEMS
        while (recentItems.size() > MAX_ITEMS) {
            recentItems.removeLast();
        }
        
        refreshItems();
    }

    private void refreshItems() {
        itemsContainer.removeAll();
        
        if (recentItems.isEmpty()) {
            JLabel emptyLabel = new JLabel("No recent items", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsContainer.add(emptyLabel);
        } else {
            for (InventoryItem item : recentItems) {
                itemsContainer.add(createItemPanel(item));
                itemsContainer.add(Box.createVerticalStrut(10)); // Spacing between items
            }
        }
        
        revalidate();
        repaint();
    }

    private JPanel createItemPanel(InventoryItem item) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new AbstractBorder() {
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
                return new Insets(10, 10, 10, 10);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        
        // Item ID and name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel idLabel = new JLabel(item.getId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        idLabel.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(idLabel, BorderLayout.EAST);
        
        // Details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 10, 2));
        detailsPanel.setOpaque(false);
        
        detailsPanel.add(createDetailLabel("Quantity:", String.valueOf(item.getQuantity())));
        detailsPanel.add(createDetailLabel("Price:", String.format("Rs %.2f", item.getPrice())));
        detailsPanel.add(createDetailLabel("Category:", item.getCategory()));
        detailsPanel.add(createDetailLabel("Value:", String.format("Rs %.2f", item.getPrice() * item.getQuantity())));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDetailLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComponent.setForeground(new Color(100, 100, 100));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(labelComponent);
        panel.add(valueComponent);
        
        return panel;
    }
} 