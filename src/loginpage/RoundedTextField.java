//RoundedTextField.java
package loginpage;

import javax.swing.*;
import java.awt.*;

/**
 * Custom Rounded Text Field Component
 * 
 * This class extends the standard JTextField to create a text field with rounded corners,
 * giving the application a more modern and polished look. It customizes the appearance
 * by overriding the paintComponent and paintBorder methods to draw rounded rectangles
 * instead of the default rectangular shape.
 * <p>
 * Features:
 * <ul>
 *     <li>Rounded corners with configurable radius</li>
 *     <li>Custom background fill</li>
 *     <li>Gray border for visual definition</li>
 *     <li>Transparent background to allow custom painting</li>
 * </ul>
 * <p>
 * This component is used throughout the application for a consistent UI style,
 * particularly in the login screen and dialog forms.
 * 
 * @author Inventory Management System
 * @version 1.0
 * @see javax.swing.JTextField
 */
public class RoundedTextField extends JTextField {
    /**
     * The radius of the rounded corners in pixels.
     * This defines how "rounded" the corners of the text field will appear.
     */
    private int radius = 15;

    /**
     * Constructs a new rounded text field with default settings.
     * <p>
     * The field is created with:
     * <ul>
     *     <li>Default size from JTextField</li>
     *     <li>Transparent background to allow custom painting</li>
     *     <li>Corner radius of 15 pixels</li>
     * </ul>
     */
    public RoundedTextField() {
        super();
        setOpaque(false); // Make the component non-opaque to allow custom background painting
    }

    /**
     * Paints the component with a rounded rectangle background.
     * <p>
     * This method overrides the default paintComponent behavior to:
     * <ol>
     *     <li>Create a Graphics2D object from the provided Graphics context</li>
     *     <li>Set the background color from the component's background property</li>
     *     <li>Fill a rounded rectangle covering the entire component area</li>
     *     <li>Call the superclass implementation to paint the text and cursor</li>
     *     <li>Clean up graphics resources</li>
     * </ol>
     * 
     * @param g The Graphics context to paint with
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
        g2.dispose();
    }

    /**
     * Paints a rounded border around the text field.
     * <p>
     * This method overrides the default paintBorder behavior to:
     * <ol>
     *     <li>Create a Graphics2D object from the provided Graphics context</li>
     *     <li>Set the border color to gray for visual definition</li>
     *     <li>Draw a rounded rectangle border around the component's perimeter</li>
     *     <li>Clean up graphics resources</li>
     * </ol>
     */
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }
    
    /**
     * Gets the current radius of the rounded corners.
     * 
     * @return The radius in pixels
     */
    public int getRadius() {
        return radius;
    }
    
    /**
     * Sets the radius of the rounded corners.
     * <p>
     * Changing this value affects how rounded the corners appear.
     * A larger value creates more rounded corners.
     * 
     * @param radius The new radius in pixels
     */
    public void setRadius(int radius) {
        this.radius = radius;
        repaint(); // Repaint the component to show the change
    }
}
