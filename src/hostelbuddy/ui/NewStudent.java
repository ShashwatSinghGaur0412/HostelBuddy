package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class NewStudent extends JFrame {
    private HashMap<String, JComponent> fields;

    public NewStudent(JFrame parent) {
        setTitle("New Student Registration");
        setSize(800, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 201, 168));

        JLabel heading = new JLabel("Register New Student");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBounds(260, 20, 400, 30);
        add(heading);

        // Field definition: label + type
        String[][] fieldData = {
            {"Student ID", "text"},
            {"Name", "text"},
            {"Mobile Number", "text"},
            {"Email", "text"},
            {"Father's Name", "text"},
            {"Mother's Name", "text"},
            {"Address", "area"},
            {"College Name", "text"},
            {"Aadhaar Number", "text"},
            {"Room Number", "combo"}
        };

        fields = new HashMap<>();
        int y = 80;

        for (String[] fd : fieldData) {
            JLabel label = new JLabel(fd[0] + ":");
            label.setBounds(100, y, 150, 25);
            add(label);

            JComponent input;

            switch (fd[1]) {
                case "combo":
                    input = new JComboBox<>();
                    loadAvailableRooms((JComboBox<String>) input);
                    break;
                case "area":
                    input = new JTextArea();
                    ((JTextArea) input).setLineWrap(true);
                    ((JTextArea) input).setWrapStyleWord(true);
                    JScrollPane scroll = new JScrollPane(input);
                    scroll.setBounds(250, y, 300, 60);
                    add(scroll);
                    fields.put(fd[0], input);
                    y += 70;
                    continue;
                default:
                    input = new JTextField();
            }

            input.setBounds(250, y, 300, 25);
            add(input);
            fields.put(fd[0], input);

            y += 40;
        }

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(250, y + 20, 120, 30);
        add(btnRegister);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(390, y + 20, 100, 30);
        add(btnClear);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 20, 100, 25);
        add(btnBack);

        btnBack.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        btnClear.addActionListener(e -> clearForm());

        btnRegister.addActionListener(e -> registerStudent());

        setVisible(true);
    }

    private void loadAvailableRooms(JComboBox<String> roomBox) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT room_number FROM rooms WHERE status = 'Not Booked' AND activate = TRUE");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomBox.addItem(rs.getString("room_number"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Room loading error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        for (String key : fields.keySet()) {
            JComponent comp = fields.get(key);
            if (comp instanceof JTextField) ((JTextField) comp).setText("");
            if (comp instanceof JTextArea) ((JTextArea) comp).setText("");
            if (comp instanceof JComboBox) ((JComboBox<?>) comp).setSelectedIndex(0);
        }
    }

    private void registerStudent() {
        try (Connection con = DBConnection.getConnection()) {
            String id = getText("Student ID");
            String name = getText("Name");
            String mobile = getText("Mobile Number");
            String email = getText("Email");
            String father = getText("Father's Name");
            String mother = getText("Mother's Name");
            String address = getText("Address");
            String college = getText("College Name");
            String aadhaar = getText("Aadhaar Number");
            String room = (String) ((JComboBox<?>) fields.get("Room Number")).getSelectedItem();

            if (id.isEmpty() || name.isEmpty() || mobile.isEmpty() || email.isEmpty() || room == null) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.");
                return;
            }

            PreparedStatement ps = con.prepareStatement("INSERT INTO students (student_id, name, mobile_number, email, father_name, mother_name, address, college_name, aadhaar_number, room_number, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Living')");
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, mobile);
            ps.setString(4, email);
            ps.setString(5, father);
            ps.setString(6, mother);
            ps.setString(7, address);
            ps.setString(8, college);
            ps.setString(9, aadhaar);
            ps.setString(10, room);

            int inserted = ps.executeUpdate();

            if (inserted > 0) {
                // Mark room as booked
                PreparedStatement roomUpdate = con.prepareStatement("UPDATE rooms SET status = 'Booked' WHERE room_number = ?");
                roomUpdate.setString(1, room);
                roomUpdate.executeUpdate();

                JOptionPane.showMessageDialog(this, "Student registered successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register student.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration error: " + ex.getMessage());
        }
    }

    private String getText(String key) {
        JComponent comp = fields.get(key);
        if (comp instanceof JTextField) return ((JTextField) comp).getText().trim();
        if (comp instanceof JTextArea) return ((JTextArea) comp).getText().trim();
        return "";
    }

    public static void main(String[] args) {
        new NewStudent(null);
    }
}
