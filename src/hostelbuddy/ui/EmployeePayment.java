package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;

public class EmployeePayment extends JFrame {
    private JComboBox<String> employeeBox, monthBox, yearBox;
    private JTextField amountField, dateField;
    private JTextArea remarksArea;
    private LinkedHashMap<String, String> employeeMap = new LinkedHashMap<>(); // name → id

    public EmployeePayment(JFrame parent) {
        setTitle("Employee Payment");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(250, 255, 240));

        JLabel heading = new JLabel("Employee Payment");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBounds(240, 20, 300, 30);
        add(heading);

        int y = 80;

        // Employee
        addLabel("Select Employee:", 100, y);
        employeeBox = new JComboBox<>();
        employeeBox.setBounds(250, y, 300, 25);
        add(employeeBox);
        loadEmployees();

        // Month
        y += 40;
        addLabel("Month:", 100, y);
        monthBox = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthBox.setBounds(250, y, 140, 25);
        add(monthBox);

        // Year
        addLabel("Year:", 410, y);
        yearBox = new JComboBox<>();
        for (int yr = 2022; yr <= LocalDate.now().getYear() + 1; yr++) {
            yearBox.addItem(String.valueOf(yr));
        }
        yearBox.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        yearBox.setBounds(460, y, 90, 25);
        add(yearBox);

        // Amount
        y += 40;
        addLabel("Amount Paid (₹):", 100, y);
        amountField = new JTextField();
        amountField.setBounds(250, y, 300, 25);
        add(amountField);

        // Payment Date
        y += 40;
        addLabel("Payment Date:", 100, y);
        dateField = new JTextField(LocalDate.now().toString());
        dateField.setBounds(250, y, 300, 25);
        add(dateField);

        // Remarks
        y += 40;
        addLabel("Remarks (optional):", 100, y);
        remarksArea = new JTextArea();
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(remarksArea);
        scroll.setBounds(250, y, 300, 60);
        add(scroll);

        // Buttons
        y += 80;
        JButton btnPay = new JButton("Submit Payment");
        btnPay.setBounds(250, y, 150, 30);
        add(btnPay);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(410, y, 100, 30);
        add(btnClear);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 20, 100, 25);
        add(btnBack);

        btnPay.addActionListener(e -> submitPayment());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        setVisible(true);
    }

    private void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 140, 25);
        add(label);
    }

    private void loadEmployees() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT employee_id, name FROM employees WHERE status = 'Working'");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("employee_id");
                String name = rs.getString("name");
                String label = name + " (" + id + ")";
                employeeBox.addItem(label);
                employeeMap.put(label, id);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage());
        }
    }

    private void submitPayment() {
        String empLabel = (String) employeeBox.getSelectedItem();
        String empId = employeeMap.get(empLabel);
        String name = empLabel.split(" \\(")[0];
        String month = (String) monthBox.getSelectedItem();
        String year = (String) yearBox.getSelectedItem();
        String amount = amountField.getText().trim();
        String date = dateField.getText().trim();
        String remarks = remarksArea.getText().trim();

        if (empId == null || amount.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO employee_payments (employee_id, name, month, year, amount, payment_date, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)"
             )) {
            ps.setString(1, empId);
            ps.setString(2, name);
            ps.setString(3, month);
            ps.setInt(4, Integer.parseInt(year));
            ps.setDouble(5, Double.parseDouble(amount));
            ps.setDate(6, Date.valueOf(date));
            ps.setString(7, remarks);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Payment recorded successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Payment failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Payment error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        employeeBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        yearBox.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        amountField.setText("");
        dateField.setText(LocalDate.now().toString());
        remarksArea.setText("");
    }

    public static void main(String[] args) {
        new EmployeePayment(null);
    }
}
