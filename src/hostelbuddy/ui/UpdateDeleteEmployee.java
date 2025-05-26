package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

public class UpdateDeleteEmployee extends JFrame {
    private HashMap<String, JComponent> fields;
    private JTextField searchField;
    private JButton btnReactivate;
    private String currentStatus = "Working";

    public UpdateDeleteEmployee(JFrame parent) {
        setTitle("Update/Delete Employee");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(255, 245, 230));

        JLabel title = new JLabel("Update/Delete Employee");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(240, 20, 400, 30);
        add(title);

        JLabel searchLabel = new JLabel("Enter Employee ID:");
        searchLabel.setBounds(80, 70, 150, 25);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(220, 70, 200, 25);
        add(searchField);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(440, 70, 100, 25);
        add(btnSearch);

        fields = new HashMap<>();
        String[] labels = {
            "Employee ID", "Name", "Gender", "Mobile Number", "Email",
            "Aadhaar Number", "Address", "Role", "Joining Date"
        };

        int y = 120;
        for (String label : labels) {
            JLabel lbl = new JLabel(label + ":");
            lbl.setBounds(80, y, 150, 25);
            add(lbl);

            JComponent field;

            if (label.equals("Gender")) {
                field = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            } else if (label.equals("Role")) {
                field = new JComboBox<>(new String[]{"Warden", "Clerk", "Cleaner", "Cook", "Security"});
            } else if (label.equals("Address")) {
                field = new JTextArea();
                ((JTextArea) field).setLineWrap(true);
                ((JTextArea) field).setWrapStyleWord(true);
                JScrollPane scroll = new JScrollPane(field);
                scroll.setBounds(220, y, 300, 60);
                add(scroll);
                fields.put(label, field);
                y += 70;
                continue;
            } else {
                field = new JTextField();
            }

            field.setBounds(220, y, 300, 25);
            add(field);
            fields.put(label, field);

            y += 40;
        }

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(220, y + 20, 100, 30);
        add(btnUpdate);

        JButton btnDelete = new JButton("Mark as Leaved");
        btnDelete.setBounds(340, y + 20, 150, 30);
        add(btnDelete);

        btnReactivate = new JButton("Mark as Working");
        btnReactivate.setBounds(500, y + 20, 150, 30);
        btnReactivate.setVisible(false);
        add(btnReactivate);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(220, y + 60, 100, 30);
        add(btnClear);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 20, 100, 25);
        add(btnBack);

        btnSearch.addActionListener(e -> searchEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> markAsLeaved());
        btnReactivate.addActionListener(e -> markAsWorking());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        setVisible(true);
    }

    private void searchEmployee() {
        String empId = searchField.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM employees WHERE employee_id = ?")) {
            ps.setString(1, empId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ((JTextField) fields.get("Employee ID")).setText(rs.getString("employee_id"));
                ((JTextField) fields.get("Name")).setText(rs.getString("name"));
                ((JComboBox<?>) fields.get("Gender")).setSelectedItem(rs.getString("gender"));
                ((JTextField) fields.get("Mobile Number")).setText(rs.getString("mobile_number"));
                ((JTextField) fields.get("Email")).setText(rs.getString("email"));
                ((JTextField) fields.get("Aadhaar Number")).setText(rs.getString("aadhaar_number"));
                ((JTextArea) fields.get("Address")).setText(rs.getString("address"));
                ((JComboBox<?>) fields.get("Role")).setSelectedItem(rs.getString("role"));
                ((JTextField) fields.get("Joining Date")).setText(rs.getString("joining_date"));
                currentStatus = rs.getString("status");

                btnReactivate.setVisible("Leaved".equalsIgnoreCase(currentStatus));
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
        }
    }

    private void updateEmployee() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE employees SET name=?, gender=?, mobile_number=?, email=?, aadhaar_number=?, address=?, role=?, joining_date=? WHERE employee_id=?"
            );

            ps.setString(1, ((JTextField) fields.get("Name")).getText().trim());
            ps.setString(2, (String) ((JComboBox<?>) fields.get("Gender")).getSelectedItem());
            ps.setString(3, ((JTextField) fields.get("Mobile Number")).getText().trim());
            ps.setString(4, ((JTextField) fields.get("Email")).getText().trim());
            ps.setString(5, ((JTextField) fields.get("Aadhaar Number")).getText().trim());
            ps.setString(6, ((JTextArea) fields.get("Address")).getText().trim());
            ps.setString(7, (String) ((JComboBox<?>) fields.get("Role")).getSelectedItem());
            ps.setString(8, ((JTextField) fields.get("Joining Date")).getText().trim());
            ps.setString(9, ((JTextField) fields.get("Employee ID")).getText().trim());

            int updated = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, updated > 0 ? "Employee updated." : "Update failed.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update error: " + ex.getMessage());
        }
    }

    private void markAsLeaved() {
        String id = ((JTextField) fields.get("Employee ID")).getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employee selected.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE employees SET status = 'Leaved' WHERE employee_id = ?")) {
            ps.setString(1, id);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, result > 0 ? "Employee marked as 'Leaved'." : "Failed to update.");
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void markAsWorking() {
        String id = ((JTextField) fields.get("Employee ID")).getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employee selected.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE employees SET status = 'Working' WHERE employee_id = ?")) {
            ps.setString(1, id);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, result > 0 ? "Employee marked as 'Working' again." : "Failed to update.");
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        for (String key : fields.keySet()) {
            JComponent comp = fields.get(key);
            if (comp instanceof JTextField) ((JTextField) comp).setText("");
            if (comp instanceof JTextArea) ((JTextArea) comp).setText("");
            if (comp instanceof JComboBox) ((JComboBox<?>) comp).setSelectedIndex(0);
        }
        searchField.setText("");
        btnReactivate.setVisible(false);
    }

    public static void main(String[] args) {
        new UpdateDeleteEmployee(null);
    }
}
