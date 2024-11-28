package OrderSystem;

// Basic imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Database imports  
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderSystem extends JFrame implements ActionListener {
    // Components
    JLabel lblTitle, lblOrderID, lblCustomerName, lblProduct, lblQuantity, lblData;
    JTextField txtOrderID, txtCustomerName, txtQuantity;
    JComboBox<String> productComboBox;
    JButton btnAdd, btnClear, btnDisplay, btnUpdate, btnDelete;
    JTextArea orderRecords;
    
    // Prices for products (hardcoded here, but you could also pull them from a database)
    String[] productPrices = {"0.0", "500.00", "300.00", "100.00", "200.00"};
    
    // Constructor
    public OrderSystem() {
        // Title with color
        lblTitle = new JLabel(":: Order System ::");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.BLUE); // Set the title color to blue

        // Labels
        lblOrderID = new JLabel("Order ID: ");
        lblCustomerName = new JLabel("Customer Name: ");
        lblProduct = new JLabel("Product: ");
        lblQuantity = new JLabel("Quantity: ");

        // Text Fields
        txtOrderID = new JTextField(20);
        txtCustomerName = new JTextField(20);
        txtQuantity = new JTextField(20);

        // Product ComboBox
        String[] products = {"Select Product", "Laptop", "Phone", "Headphones", "Tablet"};
        productComboBox = new JComboBox<>(products);

        // Buttons
        btnAdd = new JButton("Add");
        btnClear = new JButton("Clear");
        btnDisplay = new JButton("Display");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");

        // Text Area for Order Records
        lblData = new JLabel("Order Data : ");
        orderRecords = new JTextArea(10, 40);
        orderRecords.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderRecords);

        // Set layout to null
        setLayout(null);

        // Add components to the frame
        add(lblTitle);
        add(lblOrderID);
        add(txtOrderID);
        add(lblCustomerName);
        add(txtCustomerName);
        add(lblProduct);
        add(productComboBox);
        add(lblQuantity);
        add(txtQuantity);
        add(btnAdd);
        add(btnClear);
        add(btnDisplay);
        add(btnUpdate);
        add(btnDelete);
        add(lblData);
        add(scrollPane);

        // Set bounds for components
        lblTitle.setBounds(20, 20, 350, 30);
        lblOrderID.setBounds(20, 70, 100, 30);
        txtOrderID.setBounds(150, 70, 200, 30);
        lblCustomerName.setBounds(20, 110, 120, 30);
        txtCustomerName.setBounds(150, 110, 200, 30);
        lblProduct.setBounds(20, 150, 100, 30);
        productComboBox.setBounds(150, 150, 200, 30);
        lblQuantity.setBounds(20, 190, 100, 30);
        txtQuantity.setBounds(150, 190, 200, 30);
        btnAdd.setBounds(20, 230, 80, 30);
        btnClear.setBounds(120, 230, 80, 30);
        btnDisplay.setBounds(220, 230, 100, 30);
        btnUpdate.setBounds(330, 230, 100, 30);
        btnDelete.setBounds(440, 230, 100, 30);
        lblData.setBounds(20, 270, 120, 30);
        scrollPane.setBounds(150, 270, 300, 150);

        // Action listeners for buttons
        btnAdd.addActionListener(this);
        btnClear.addActionListener(this);
        btnDisplay.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);

        // Frame settings
        setTitle("Order System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Action performed when buttons are clicked
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            handleAddOrder();
        } else if (e.getSource() == btnClear) {
            handleClear();
        } else if (e.getSource() == btnDisplay) {
            loadOrderRecords();
        } else if (e.getSource() == btnUpdate) {
            handleUpdateOrder();
        } else if (e.getSource() == btnDelete) {
            handleDeleteOrder();
        }
    }

    // Add order to database and display it
    private void handleAddOrder() {
        String orderID = txtOrderID.getText();
        String customerName = txtCustomerName.getText();
        String product = (String) productComboBox.getSelectedItem();
        String quantity = txtQuantity.getText();

        // Get the price for the selected product
        String price = getPriceForProduct(product);

        // Validation for empty fields
        if (orderID.isEmpty() || customerName.isEmpty() || product.equals("Select Product") ||
            quantity.isEmpty()) {
            showError("Please fill in all fields correctly.");
            return;
        }

        // Database connection
        String url = "jdbc:mysql://localhost:3306/order_db";  // Database connection URL
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO orders (order_id, customer_name, product, quantity, price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, orderID);
                pstmt.setString(2, customerName);
                pstmt.setString(3, product);
                pstmt.setString(4, quantity);
                pstmt.setString(5, price);
                pstmt.executeUpdate();
            }

            // Display the new record in the JTextArea
            orderRecords.append("Order added:\n" +
                    "Order ID: " + orderID +
                    "\nCustomer Name: " + customerName +
                    "\nProduct: " + product +
                    "\nQuantity: " + quantity +
                    "\nPrice: " + price + "\n\n");

            // Show success confirmation
            JOptionPane.showMessageDialog(this, "Order added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    // Get the price for the selected product
    private String getPriceForProduct(String product) {
        int productIndex = productComboBox.getSelectedIndex();
        if (productIndex > 0) {  // Ensures it's a valid product selected
            return productPrices[productIndex];  // Return the price corresponding to the product
        } else {
            return "0.0";  // Default price if no product is selected
        }
    }

    // Clear input fields
    private void handleClear() {
        txtOrderID.setText("");
        txtCustomerName.setText("");
        productComboBox.setSelectedIndex(0);  // Reset product dropdown
        txtQuantity.setText("");
    }

    // Load order records
    private void loadOrderRecords() {
        // Clear current records
        orderRecords.setText("");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/order_db";  // Database connection URL
        String user = "root";
        String password = "";

        // Query to retrieve order records
        String sql = "SELECT order_id, customer_name, product, quantity, price FROM orders";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Append all records to the JTextArea
            while (rs.next()) {
                String orderID = rs.getString("order_id");
                String customerName = rs.getString("customer_name");
                String product = rs.getString("product");
                String quantity = rs.getString("quantity");
                String price = rs.getString("price");
                orderRecords.append("Order ID: " + orderID + "\nCustomer Name: " + customerName + 
                                    "\nProduct: " + product + "\nQuantity: " + quantity + 
                                    "\nPrice: " + price + "\n\n");
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    // Update order
    private void handleUpdateOrder() {
        String orderID = txtOrderID.getText();
        // Additional update logic goes here
    }

    // Delete order
    private void handleDeleteOrder() {
        String orderID = txtOrderID.getText();
        // Additional delete logic goes here
    }

    // Show error message
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new OrderSystem();  // Launch the application
    }
}
