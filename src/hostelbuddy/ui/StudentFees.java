package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentFees extends JFrame {
    private JTextField idField, nameField, emailField, roomField, amountField;
    private JComboBox<String> monthDropdown;

    public StudentFees(JFrame parent) {
        setTitle("Student Fee Management");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        Color bgColor = new Color(250, 240, 230);
        getContentPane().setBackground(bgColor);

        JLabel title = new JLabel("Student Fee Payment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(200, 20, 300, 30);
        add(title);

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setBounds(50, 80, 100, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(160, 80, 200, 25);
        add(idField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(380, 80, 100, 25);
        add(searchBtn);

        searchBtn.addActionListener(e -> searchStudent());

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 120, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(160, 120, 320, 25);
        nameField.setEditable(false);
        add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 160, 100, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(160, 160, 320, 25);
        emailField.setEditable(false);
        add(emailField);

        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setBounds(50, 200, 100, 25);
        add(roomLabel);

        roomField = new JTextField();
        roomField.setBounds(160, 200, 320, 25);
        roomField.setEditable(false);
        add(roomField);

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setBounds(50, 240, 100, 25);
        add(monthLabel);

        monthDropdown = new JComboBox<>(new String[] {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthDropdown.setBounds(160, 240, 200, 25);
        add(monthDropdown);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(50, 280, 100, 25);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(160, 280, 200, 25);
        add(amountField);

        JButton payBtn = new JButton("Save Payment");
        payBtn.setBounds(160, 330, 140, 30);
        add(payBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(320, 330, 100, 30);
        add(clearBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(540, 20, 100, 25);
        add(backBtn);

        clearBtn.addActionListener(e -> clearForm());
        backBtn.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        payBtn.addActionListener(e -> savePayment());

        setVisible(true);
    }

    private void searchStudent() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student ID.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT name, email, room_number FROM students WHERE student_id = ?");
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                roomField.setText(rs.getString("room_number"));
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void savePayment() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String room = roomField.getText().trim();
        String month = monthDropdown.getSelectedItem().toString();
        String amount = amountField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || room.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields before saving.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement checkStmt = con.prepareStatement(
                "SELECT * FROM fees WHERE student_id = ? AND month = ?");
            checkStmt.setString(1, id);
            checkStmt.setString(2, month);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Payment already made for this month.");
                return;
            }

            PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO fees (student_id, name, email, room_number, month, amount) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, room);
            stmt.setString(5, month);
            stmt.setString(6, amount);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Payment saved successfully.");
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        roomField.setText("");
        amountField.setText("");
        monthDropdown.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new StudentFees(null);
    }
}
