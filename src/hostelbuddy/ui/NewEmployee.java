package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;

public class NewEmployee extends JFrame {
    private HashMap<String, JComponent> fields;

    public NewEmployee(JFrame parent) {
        setTitle("New Employee Registration");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 255, 250));

        JLabel heading = new JLabel("Register New Employee");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBounds(250, 20, 400, 30);
        add(heading);

        // Field labels and their types
        String[][] fieldData = {
            {"Employee ID", "text"},
            {"Name", "text"},
            {"Gender", "combo"},
            {"Mobile Number", "text"},
            {"Email", "text"},
            {"Aadhaar Number", "text"},
            {"Address", "area"},
            {"Role", "combo-role"},
            {"Joining Date", "date"}
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
                    input = new JComboBox<>(new String[]{"Male", "Female", "Other"});
                    break;
                case "combo-role":
                    input = new JComboBox<>(new String[]{"Warden", "Clerk", "Cleaner", "Cook", "Security"});
                    break;
                case "area":
                    input = new JTextArea();
                    ((JTextArea) input).setLineWrap(true);
                    ((JTextArea) input).setWrapStyleWord(true);
                    JScrollPane scroll = new JScrollPane(input);
                    scroll.setBounds(250, y, 300, 60);
                    add(scroll);
                    fields.put(fd[0], input);
                    y += 70; // ✅ Enough space for address
                    continue;
                case "date":
                    input = new JTextField(LocalDate.now().toString());
                    break;
                default:
                    input = new JTextField();
            }

            input.setBounds(250, y, 300, 25);
            add(input);
            fields.put(fd[0], input);

            y += 40;
        }

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(250, y + 20, 120, 30);
        add(registerBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(390, y + 20, 100, 30);
        add(clearBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(20, 20, 100, 25);
        add(backBtn);

        backBtn.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        clearBtn.addActionListener(e -> clearForm());

        registerBtn.addActionListener(e -> registerEmployee());

        setVisible(true);
    }

    private void clearForm() {
        for (String key : fields.keySet()) {
            JComponent comp = fields.get(key);
            if (comp instanceof JTextField) ((JTextField) comp).setText("");
            if (comp instanceof JTextArea) ((JTextArea) comp).setText("");
            if (comp instanceof JComboBox) ((JComboBox<?>) comp).setSelectedIndex(0);
        }
        ((JTextField) fields.get("Joining Date")).setText(LocalDate.now().toString());
    }

    private void registerEmployee() {
        String id = ((JTextField) fields.get("Employee ID")).getText().trim();
        String name = ((JTextField) fields.get("Name")).getText().trim();
        String gender = (String) ((JComboBox<?>) fields.get("Gender")).getSelectedItem();
        String mobile = ((JTextField) fields.get("Mobile Number")).getText().trim();
        String email = ((JTextField) fields.get("Email")).getText().trim();
        String aadhaar = ((JTextField) fields.get("Aadhaar Number")).getText().trim();
        String address = ((JTextArea) fields.get("Address")).getText().trim();
        String role = (String) ((JComboBox<?>) fields.get("Role")).getSelectedItem();
        String joinDate = ((JTextField) fields.get("Joining Date")).getText().trim();

        if (id.isEmpty() || name.isEmpty() || mobile.isEmpty() || email.isEmpty() ||
                aadhaar.isEmpty() || address.isEmpty() || joinDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO employees (employee_id, name, gender, mobile_number, email, aadhaar_number, address, role, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, gender);
            ps.setString(4, mobile);
            ps.setString(5, email);
            ps.setString(6, aadhaar);
            ps.setString(7, address);
            ps.setString(8, role);
            ps.setString(9, joinDate);

            int inserted = ps.executeUpdate();
            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "Employee registered successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register employee.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new NewEmployee(null);
    }
}
