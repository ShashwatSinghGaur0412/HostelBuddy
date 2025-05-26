package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UpdateDeleteStudent extends JFrame {
    private JTextField[] fields;
    private JComboBox<String> roomDropdown;
    private JTextField searchField;
    private JButton btnReactivate;
    private String currentStatus = "Living";

    private final String[] labels = {
        "Student ID / Roll No", "Name", "Mobile Number", "Email",
        "Father’s Name", "Mother’s Name", "Address", "College Name",
        "Aadhaar Number", "Room Number"
    };

    public UpdateDeleteStudent(JFrame parent) {
        setTitle("Update/Delete Student");
        setSize(850, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(250, 240, 230));

        JLabel heading = new JLabel("Update/Delete Student");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBounds(280, 20, 400, 30);
        add(heading);

        JLabel lblSearch = new JLabel("Enter Student ID:");
        lblSearch.setBounds(100, 70, 140, 25);
        add(lblSearch);

        searchField = new JTextField();
        searchField.setBounds(240, 70, 250, 25);
        add(searchField);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(510, 70, 100, 25);
        add(btnSearch);

        fields = new JTextField[labels.length - 1];
        roomDropdown = new JComboBox<>();
        int y = 120;

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i] + ":");
            lbl.setBounds(100, y, 160, 25);
            add(lbl);

            if (i == labels.length - 1) {
                roomDropdown.setBounds(270, y, 300, 25);
                add(roomDropdown);
            } else {
                fields[i] = new JTextField();
                fields[i].setBounds(270, y, 300, 25);
                add(fields[i]);
            }

            y += 40;
        }

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(170, y + 20, 100, 30);
        add(btnUpdate);

        JButton btnDelete = new JButton("Mark as Leaved");
        btnDelete.setBounds(280, y + 20, 150, 30);
        add(btnDelete);

        btnReactivate = new JButton("Mark as Living");
        btnReactivate.setBounds(440, y + 20, 150, 30);
        btnReactivate.setVisible(false);
        add(btnReactivate);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(600, y + 20, 100, 30);
        add(btnClear);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 20, 100, 25);
        add(btnBack);

        btnBack.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        btnSearch.addActionListener(e -> searchStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> markAsLeaved());
        btnReactivate.addActionListener(e -> markAsLiving());
        btnClear.addActionListener(e -> clearForm());

        loadAvailableRooms();
        setVisible(true);
    }

    private void loadAvailableRooms() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT room_number FROM rooms WHERE activate = true AND status = 'Not Booked'")) {
            ResultSet rs = stmt.executeQuery();
            roomDropdown.removeAllItems();
            while (rs.next()) {
                roomDropdown.addItem(rs.getString("room_number"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + ex.getMessage());
        }
    }

    private void searchStudent() {
        String id = searchField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID to search.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM students WHERE student_id = ?")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText(rs.getString(i + 1));
                }

                currentStatus = rs.getString("status");
                String studentRoom = rs.getString("room_number");

                roomDropdown.removeAllItems();
                try (PreparedStatement roomStmt = con.prepareStatement("SELECT room_number FROM rooms WHERE activate = true AND status = 'Not Booked'");
                     ResultSet roomSet = roomStmt.executeQuery()) {
                    while (roomSet.next()) {
                        roomDropdown.addItem(roomSet.getString("room_number"));
                    }
                }

                // Ensure student room is present
                if (((DefaultComboBoxModel<String>) roomDropdown.getModel()).getIndexOf(studentRoom) == -1) {
                    roomDropdown.addItem(studentRoom);
                }
                roomDropdown.setSelectedItem(studentRoom);

                btnReactivate.setVisible("Leaved".equalsIgnoreCase(currentStatus));
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        try (Connection con = DBConnection.getConnection()) {
            String id = searchField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No student selected to update.");
                return;
            }

            String updateSQL = "UPDATE students SET name=?, mobile_number=?, email=?, father_name=?, mother_name=?, address=?, college_name=?, aadhaar_number=?, room_number=? WHERE student_id=?";
            PreparedStatement ps = con.prepareStatement(updateSQL);
            for (int i = 1; i <= fields.length; i++) {
                ps.setString(i, fields[i - 1].getText().trim());
            }
            ps.setString(9, roomDropdown.getSelectedItem().toString());
            ps.setString(10, id);

            int rows = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, rows > 0 ? "Student updated successfully." : "Update failed.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update error: " + ex.getMessage());
        }
    }

    private void markAsLeaved() {
        String id = searchField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID to mark as leaved.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement getRoom = con.prepareStatement("SELECT room_number FROM students WHERE student_id = ?");
            getRoom.setString(1, id);
            ResultSet rs = getRoom.executeQuery();

            String room = rs.next() ? rs.getString("room_number") : null;

            PreparedStatement leaveStmt = con.prepareStatement("UPDATE students SET status = 'Leaved' WHERE student_id = ?");
            leaveStmt.setString(1, id);
            int updated = leaveStmt.executeUpdate();

            if (updated > 0 && room != null) {
                PreparedStatement freeRoom = con.prepareStatement("UPDATE rooms SET status = 'Not Booked' WHERE room_number = ?");
                freeRoom.setString(1, room);
                freeRoom.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student marked as 'Leaved'. Room freed.");
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete error: " + ex.getMessage());
        }
    }

    private void markAsLiving() {
        String id = searchField.getText().trim();
        String newRoom = roomDropdown.getSelectedItem().toString();

        if (id.isEmpty() || newRoom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a room before reactivating.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE students SET status = 'Living', room_number = ? WHERE student_id = ?");
            ps.setString(1, newRoom);
            ps.setString(2, id);
            ps.executeUpdate();

            PreparedStatement updateRoom = con.prepareStatement("UPDATE rooms SET status = 'Booked' WHERE room_number = ?");
            updateRoom.setString(1, newRoom);
            updateRoom.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student reactivated successfully.");
            clearForm();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        for (JTextField field : fields) field.setText("");
        roomDropdown.removeAllItems();
        searchField.setText("");
        btnReactivate.setVisible(false);
    }

    public static void main(String[] args) {
        new UpdateDeleteStudent(null);
    }
}
