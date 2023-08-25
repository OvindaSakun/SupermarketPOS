import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.util.HashMap;
import java.util.ArrayList;

public class SupermarketPOS extends JFrame {

    private static class Item {
        String name;
        double price;

        Item(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private HashMap<String, Item> inventory;
    private ArrayList<Item> cart;
    private double total;

    // UI Elements
    private JTextField itemCodeField;
    private JTextArea cartArea;
    private JLabel totalLabel;
    private JTextArea inventoryArea;

    // New Inventory UI Elements
    private JTextField newCodeField, newNameField, newPriceField;

    public SupermarketPOS() {
        setTitle("Supermarket POS System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2, 15, 15));

        inventory = new HashMap<>();
        cart = new ArrayList<>();
        total = 0.0;

        // Populate Initial Inventory
        inventory.put("001", new Item("Apples (1kg)", 3.50));
        inventory.put("002", new Item("Bread", 2.20));
        inventory.put("003", new Item("Milk (1L)", 1.80));
        inventory.put("004", new Item("Eggs (12)", 4.00));
        inventory.put("005", new Item("Coffee", 6.50));

        setupUI();
        refreshInventoryDisplay();
    }

    private void setupUI() {
        // --- LEFT PANEL: Input & Inventory ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Add to Cart Section
        JLabel inputLabel = new JLabel("Add Item to Cart");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel cartInputPanel = new JPanel(new FlowLayout());
        cartInputPanel.add(new JLabel("Item Code:"));
        itemCodeField = new JTextField(10);
        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(this::addItemToCart);
        cartInputPanel.add(itemCodeField);
        cartInputPanel.add(addButton);

        // 2. Add New Inventory Item Section
        JLabel newInvLabel = new JLabel("Add New Inventory Item");
        newInvLabel.setFont(new Font("Arial", Font.BOLD, 16));
        newInvLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel newInvPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        newInvPanel.setMaximumSize(new Dimension(300, 90));
        
        newInvPanel.add(new JLabel("New Code:"));
        newCodeField = new JTextField();
        newInvPanel.add(newCodeField);
        
        newInvPanel.add(new JLabel("Item Name:"));
        newNameField = new JTextField();
        newInvPanel.add(newNameField);
        
        newInvPanel.add(new JLabel("Price ($):"));
        newPriceField = new JTextField();
        newInvPanel.add(newPriceField);

        JButton addInvButton = new JButton("Save to Inventory");
        addInvButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addInvButton.addActionListener(this::addNewInventoryItem);

        // 3. Inventory Display Section
        JLabel inventoryLabel = new JLabel("Available Inventory:");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inventoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        inventoryArea = new JTextArea();
        inventoryArea.setEditable(false);
        inventoryArea.setBackground(new Color(240, 240, 240));
        JScrollPane invScrollPane = new JScrollPane(inventoryArea);

        // Assemble Left Panel
        leftPanel.add(inputLabel);
        leftPanel.add(cartInputPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(new JSeparator());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(newInvLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(newInvPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(addInvButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(new JSeparator());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(inventoryLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(invScrollPane);

        // --- RIGHT PANEL: Cart & Checkout ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel cartTitle = new JLabel("Shopping Cart", SwingConstants.CENTER);
        cartTitle.setFont(new Font("Arial", Font.BOLD, 18));

        cartArea = new JTextArea();
        cartArea.setEditable(false);
        cartArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane cartScrollPane = new JScrollPane(cartArea);

        totalLabel = new JLabel("Total: $0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalLabel.setForeground(new Color(47, 165, 114));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(e -> clearCart());
        
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> checkout());

        JButton printButton = new JButton("Print / Export PDF");
        printButton.addActionListener(e -> printReceipt());
        
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(printButton);

        rightPanel.add(cartTitle, BorderLayout.NORTH);
        rightPanel.add(cartScrollPane, BorderLayout.CENTER);
        
        JPanel bottomRight = new JPanel(new BorderLayout(0, 15));
        bottomRight.add(totalLabel, BorderLayout.NORTH);
        bottomRight.add(buttonPanel, BorderLayout.SOUTH);
        rightPanel.add(bottomRight, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);
        
        updateCartDisplay();
    }

    private void refreshInventoryDisplay() {
        StringBuilder invText = new StringBuilder();
        for (String code : inventory.keySet()) {
            Item item = inventory.get(code);
            invText.append(String.format("[%s] %s - $%.2f\n", code, item.name, item.price));
        }
        inventoryArea.setText(invText.toString());
    }

    private void addNewInventoryItem(ActionEvent e) {
        String code = newCodeField.getText().trim();
        String name = newNameField.getText().trim();
        String priceText = newPriceField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields to add an item.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            inventory.put(code, new Item(name, price));
            refreshInventoryDisplay();
            
            // Clear input fields
            newCodeField.setText("");
            newNameField.setText("");
            newPriceField.setText("");
            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addItemToCart(ActionEvent e) {
        String code = itemCodeField.getText().trim();
        if (inventory.containsKey(code)) {
            Item item = inventory.get(code);
            cart.add(item);
            total += item.price;
            updateCartDisplay();
            itemCodeField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Item code not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCartDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- RECEIPT ---\n\n");
        for (Item item : cart) {
            sb.append(String.format("%-25s $%.2f\n", item.name, item.price));
        }
        sb.append(String.format("\nTOTAL: $%.2f\n", total));
        sb.append("----------------");
        
        cartArea.setText(sb.toString());
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void clearCart() {
        cart.clear();
        total = 0.0;
        updateCartDisplay();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, cartArea.getText(), "Checkout Successful", JOptionPane.INFORMATION_MESSAGE);
        clearCart();
    }

    private void printReceipt() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Nothing to print.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            boolean complete = cartArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Export/Print Complete", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this, "Export Failed: " + pe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SupermarketPOS().setVisible(true);
        });
    }
}