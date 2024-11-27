package StudentAttendanceSystem;

// basic import
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// database import
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentAttendanceSystem extends JFrame implements ActionListener {
    // Components
    JLabel lblTitle, lblStudentID, lblName, lblStatus, lblDepartment, lblSemester, lblData;
    JTextField txtStudentID, txtName;
    JRadioButton rbPresent, rbAbsent;
    JButton btnAdd, btnClear, btnDisplay, btnUpdate, btnDelete;
    JTextArea attendanceRecords;
    ButtonGroup statusGroup;  // Group for radio buttons
    JComboBox<String> departmentComboBox, semesterComboBox;

    // Constructor and labels name to items
    public StudentAttendanceSystem() {
        // Title with color
        lblTitle = new JLabel(":: Student Attendance System ::");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.BLUE); // Set the title color to blue

        // Labels
        lblStudentID = new JLabel("Student ID: ");
        lblName = new JLabel("Name: ");
        lblStatus = new JLabel("Attendance Status: ");
        lblDepartment = new JLabel("Department: ");
        lblSemester = new JLabel("Semester: ");

        // Text Fields
        txtStudentID = new JTextField(20);
        txtName = new JTextField(20);

        // Radio buttons for Attendance Status
        rbPresent = new JRadioButton("Present");
        rbAbsent = new JRadioButton("Absent");

        // Group the radio buttons so only one can be selected
        statusGroup = new ButtonGroup();
        statusGroup.add(rbPresent);
        statusGroup.add(rbAbsent);

        // Set Present as the default selection
        rbPresent.setSelected(true);

        // Department ComboBox
        String[] departments = {"Select Department", "JTMK", "JKE", "JKM", "JP", "JJJJ"};
        departmentComboBox = new JComboBox<>(departments);

        // Semester ComboBox with values 1 to 9
        String[] semesters = {"Select Semester", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        semesterComboBox = new JComboBox<>(semesters);

        // Buttons
        btnAdd = new JButton("Add");
        btnClear = new JButton("Clear");
        btnDisplay = new JButton("Display");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");

        // Text Area for Attendance Records
        lblData = new JLabel("Student Data : ");
        attendanceRecords = new JTextArea(10, 40);
        attendanceRecords.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(attendanceRecords);

        // Set layout to null 
        setLayout(null);

        // Add components to the frame
        add(lblTitle);
        add(lblStudentID);
        add(txtStudentID);
        add(lblName);
        add(txtName);
        add(lblStatus);
        add(rbPresent);
        add(rbAbsent);
        add(lblDepartment);
        add(departmentComboBox);
        add(lblSemester);
        add(semesterComboBox);
        add(btnAdd);
        add(btnClear);
        add(btnDisplay);
        add(btnUpdate);
        add(btnDelete);
        add(lblData);
        add(scrollPane);

        // Set bounds for components
        lblTitle.setBounds(20, 20, 350, 30);
        lblStudentID.setBounds(20, 70, 100, 30);
        txtStudentID.setBounds(150, 70, 200, 30);
        lblName.setBounds(20, 110, 100, 30);
        txtName.setBounds(150, 110, 200, 30);
        lblStatus.setBounds(20, 150, 120, 30);
        rbPresent.setBounds(150, 150, 100, 30);
        rbAbsent.setBounds(260, 150, 100, 30);
        lblDepartment.setBounds(20, 190, 100, 30);
        departmentComboBox.setBounds(150, 190, 200, 30);
        lblSemester.setBounds(20, 230, 100, 30);
        semesterComboBox.setBounds(150, 230, 200, 30);
        btnAdd.setBounds(20, 270, 80, 30);
        btnClear.setBounds(120, 270, 80, 30);
        btnDisplay.setBounds(220, 270, 100, 30);
        btnUpdate.setBounds(330, 270, 100, 30);
        btnDelete.setBounds(440, 270, 100, 30);
        lblData.setBounds(20,310,120,30);
        scrollPane.setBounds(150, 310, 300, 150);

        // Action listeners for buttons
        btnAdd.addActionListener(this);
        btnClear.addActionListener(this);
        btnDisplay.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);

        // Frame settings
        setTitle("Student Attendance System");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Action performed when buttons are clicked
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            handleAddAttendance();
        } else if (e.getSource() == btnClear) {
            handleClear();
        } else if (e.getSource() == btnDisplay) {
            loadAttendanceRecords();
        } else if (e.getSource() == btnUpdate) {
            handleUpdateAttendance();
        } else if (e.getSource() == btnDelete) {
            handleDeleteAttendance();
        }
    }

    // add attendance
    private void handleAddAttendance() {
        String studentID = txtStudentID.getText();
        String name = txtName.getText();
        String status = rbPresent.isSelected() ? "Present" : "Absent";
        String department = (String) departmentComboBox.getSelectedItem();
        String semester = (String) semesterComboBox.getSelectedItem();

        // Validation for empty spaces
        if (studentID.isEmpty() || name.isEmpty() || department.equals("Select Department") || semester.equals("Select Semester")) {
            showError("Please fill in all fields correctly.");
            return;
        }

        // Name validation - only letters and spaces allowed
        if (!name.matches("^[a-zA-Z ]+$")) {
            showError("Please enter a valid name with only letters and spaces.");
            return;
        }

        // Database connection
        String url = "jdbc:mysql://localhost:3306/attendance_db";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO attendance (student_id, name, status, department, semester) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentID);
                pstmt.setString(2, name);
                pstmt.setString(3, status);
                pstmt.setString(4, department);
                pstmt.setString(5, semester);
                pstmt.executeUpdate();
            }

            // Display the new record in the JTextArea
            attendanceRecords.append("Record added:\n" +
                    "ID: " + studentID +
                    "\nName: " + name +
                    "\nStatus: " + status +
                    "\nDepartment: " + department +
                    "\nSemester: " + semester + "\n\n");

            // Show success confirmation
            JOptionPane.showMessageDialog(this, "Attendance added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void handleClear() {
        txtStudentID.setText("");
        txtName.setText("");
        departmentComboBox.setSelectedIndex(0);  // Reset department dropdown
        semesterComboBox.setSelectedIndex(0);  // Reset semester dropdown
        statusGroup.clearSelection();  // Clear the radio button selection
        rbPresent.setSelected(true);  // Optionally set Present as default
    }

    private void loadAttendanceRecords() {
        // Clear the current records
        attendanceRecords.setText("");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/attendance_db";
        String user = "root";
        String password = "";

        // Query to retrieve attendance records
        String sql = "SELECT student_id, name, status, department, semester FROM attendance";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Append all records to the JTextArea
            while (rs.next()) {
                String studentID = rs.getString("student_id");
                String name = rs.getString("name");
                String status = rs.getString("status");
                String department = rs.getString("department");
                String semester = rs.getString("semester");
                attendanceRecords.append("ID: " + studentID + "\nName: " + name + "\nStatus: " + status + "\nDepartment: " + department + "\nSemester: " + semester + "\n\n");
            }

        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void handleUpdateAttendance() {
        String studentID = txtStudentID.getText();
        String name = txtName.getText();
        String status = rbPresent.isSelected() ? "Present" : "Absent";
        String department = (String) departmentComboBox.getSelectedItem();
        String semester = (String) semesterComboBox.getSelectedItem();

        if (studentID.isEmpty() || name.isEmpty() || department.equals("Select Department") || semester.equals("Select Semester")) {
            showError("Please fill in all fields correctly.");
            return;
        }

        // Name validation - only letters and spaces allowed
        if (!name.matches("^[a-zA-Z ]+$")) {
            showError("Please enter a valid name with only letters and spaces.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/attendance_db";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "UPDATE attendance SET name = ?, status = ?, department = ?, semester = ? WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, status);
                pstmt.setString(3, department);
                pstmt.setString(4, semester);
                pstmt.setString(5, studentID);

                int updatedRows = pstmt.executeUpdate();
                if (updatedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Attendance updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "match the student_id to update.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void handleDeleteAttendance() {
        String studentID = txtStudentID.getText();

        if (studentID.isEmpty()) {
            showError("Please enter a student ID to delete.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/attendance_db";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "DELETE FROM attendance WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentID);
                int deletedRows = pstmt.executeUpdate();

                if (deletedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Attendance deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "match the student_id record to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new StudentAttendanceSystem();
    }
}
