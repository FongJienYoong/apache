import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BMICalculator extends JFrame {

    // UI Components without access modifiers
    JLabel lblName, lblStudentID, lblGender, lblClass, lblWeight, lblHeight;
    JTextField txtName, txtStudentID, txtWeight, txtHeight;
    JTextArea resultArea;
    JComboBox<String> classCombo;
    JRadioButton maleButton, femaleButton;
    ButtonGroup genderGroup;
    JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // Database connection
    Connection conn;

    // Constructor
    public BMICalculator() {
        // Set up JFrame
        setTitle("BMI Calculator");
        setLayout(null);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize components
        initializeComponents();

        // Set up database connection
        connectToDatabase();

        // Add action listeners
        btnAdd.addActionListener(e -> handleAddRecord());
        btnUpdate.addActionListener(e -> handleUpdateRecord());
        btnDelete.addActionListener(e -> handleDeleteRecord());
        btnClear.addActionListener(e -> clearFields());

        setVisible(true);
    }

    // Initialize UI components and their layout
    private void initializeComponents() {
        lblName = new JLabel("Name:");
        lblStudentID = new JLabel("Student ID:");
        lblGender = new JLabel("Gender:");
        lblClass = new JLabel("Class:");
        lblWeight = new JLabel("Weight (kg):");
        lblHeight = new JLabel("Height (m):");

        txtName = new JTextField();
        txtStudentID = new JTextField();
        txtWeight = new JTextField();
        txtHeight = new JTextField();

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        classCombo = new JComboBox<>(new String[] {"Please select class", "A", "B", "C", "D"});
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        // Set bounds for components
        lblName.setBounds(30, 30, 100, 30);
        txtName.setBounds(150, 30, 200, 30);

        lblStudentID.setBounds(30, 70, 100, 30);
        txtStudentID.setBounds(150, 70, 200, 30);

        lblGender.setBounds(30, 110, 100, 30);
        maleButton.setBounds(150, 110, 60, 30);
        femaleButton.setBounds(220, 110, 80, 30);

        lblClass.setBounds(30, 150, 100, 30);
        classCombo.setBounds(150, 150, 200, 30);

        lblWeight.setBounds(30, 190, 100, 30);
        txtWeight.setBounds(150, 190, 200, 30);

        lblHeight.setBounds(30, 230, 100, 30);
        txtHeight.setBounds(150, 230, 200, 30);

        btnAdd.setBounds(30, 270, 80, 30);
        btnUpdate.setBounds(120, 270, 80, 30);
        btnDelete.setBounds(210, 270, 80, 30);
        btnClear.setBounds(300, 270, 80, 30);

        resultArea.setBounds(30, 310, 350, 100);

        // Add components to the frame
        add(lblName);
        add(txtName);
        add(lblStudentID);
        add(txtStudentID);
        add(lblGender);
        add(maleButton);
        add(femaleButton);
        add(lblClass);
        add(classCombo);
        add(lblWeight);
        add(txtWeight);
        add(lblHeight);
        add(txtHeight);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);
        add(resultArea);
    }

    // Connect to the database
    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database.");
        }
    }

    // Handle add record button action
    private void handleAddRecord() {
        if (validateFields()) {
            String name = txtName.getText();
            String studentID = txtStudentID.getText();
            String gender = maleButton.isSelected() ? "Male" : "Female";
            String studentClass = (String) classCombo.getSelectedItem();
            double weight = Double.parseDouble(txtWeight.getText());
            double height = Double.parseDouble(txtHeight.getText());
            double bmi = calculateBMI(weight, height);

            // Save to database
            executeDatabaseOperation("INSERT INTO bmi_records (student_id, name, gender, class, weight, height, bmi) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    studentID, name, gender, studentClass, weight, height, bmi);

            resultArea.setText("BMI: " + bmi);
        }
    }

    // Handle update record button action
    private void handleUpdateRecord() {
        if (validateFields()) {
            String name = txtName.getText();
            String studentID = txtStudentID.getText();
            String gender = maleButton.isSelected() ? "Male" : "Female";
            String studentClass = (String) classCombo.getSelectedItem();
            double weight = Double.parseDouble(txtWeight.getText());
            double height = Double.parseDouble(txtHeight.getText());
            double bmi = calculateBMI(weight, height);

            // Update database
            executeDatabaseOperation("UPDATE bmi_records SET name = ?, gender = ?, class = ?, weight = ?, height = ?, bmi = ? WHERE student_id = ?",
                    name, gender, studentClass, weight, height, bmi, studentID);

            resultArea.setText("BMI: " + bmi);
        }
    }

    // Handle delete record button action
    private void handleDeleteRecord() {
        String studentID = txtStudentID.getText();
        if (!studentID.isEmpty()) {
            executeDatabaseOperation("DELETE FROM bmi_records WHERE student_id = ?", studentID);
            JOptionPane.showMessageDialog(this, "Record deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid Student ID.");
        }
    }

    // Validate input fields
    private boolean validateFields() {
        if (txtName.getText().isEmpty() || txtStudentID.getText().isEmpty() || txtWeight.getText().isEmpty() || txtHeight.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.");
            return false;
        }
        if (classCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a class.");
            return false;
        }
        return true;
    }

    // Calculate BMI
    private double calculateBMI(double weight, double height) {
        return weight / (height * height);
    }

    // Execute database operations (add, update, delete)
    private void executeDatabaseOperation(String query, Object... params) {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Operation successful.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database operation failed.");
        }
    }

    // Clear fields
    private void clearFields() {
        txtName.setText("");
        txtStudentID.setText("");
        txtWeight.setText("");
        txtHeight.setText("");
        genderGroup.clearSelection();
        classCombo.setSelectedIndex(0);
        resultArea.setText("");
    }

    public static void main(String[] args) {
        new BMICalculator();
    }
}
