package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;
import hostelbuddy.utils.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;

public class EmployeePaymentHistory extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> employeeBox, monthBox, yearBox;
    private JTextField searchField;
    private LinkedHashMap<String, String> employeeMap = new LinkedHashMap<>();

    public EmployeePaymentHistory(JFrame parent) {
        setTitle("Employee Payment History");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 250, 255));

        JLabel heading = new JLabel("Employee Payment History");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBounds(320, 20, 400, 30);
        add(heading);

        JLabel lblEmp = new JLabel("Employee:");
        lblEmp.setBounds(50, 70, 80, 25);
        add(lblEmp);
        employeeBox = new JComboBox<>();
        employeeBox.setBounds(130, 70, 220, 25);
        add(employeeBox);
        loadEmployees();

        JLabel lblMonth = new JLabel("Month:");
        lblMonth.setBounds(370, 70, 60, 25);
        add(lblMonth);
        monthBox = new JComboBox<>(new String[]{
            "All", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthBox.setBounds(430, 70, 100, 25);
        add(monthBox);

        JLabel lblYear = new JLabel("Year:");
        lblYear.setBounds(540, 70, 50, 25);
        add(lblYear);
        yearBox = new JComboBox<>();
        yearBox.addItem("All");
        for (int yr = 2022; yr <= java.time.LocalDate.now().getYear(); yr++) {
            yearBox.addItem(String.valueOf(yr));
        }
        yearBox.setBounds(590, 70, 80, 25);
        add(yearBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(690, 70, 100, 25);
        add(searchBtn);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBounds(800, 70, 140, 25);
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(870, 20, 100, 25);
        add(backBtn);

        model = new DefaultTableModel(new String[]{
            "ID", "Employee ID", "Name", "Month", "Year", "Amount", "Payment Date", "Remarks"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 120, 880, 400);
        add(scrollPane);

        searchBtn.addActionListener(e -> loadData());
        exportBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".csv")) path += ".csv";
                ExportUtil.exportTableToCSV(table, path);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            if (parent != null) {
                parent.setEnabled(true);
                parent.toFront();
            }
        });

        loadData(); // Load all by default
        setVisible(true);
    }

    private void loadEmployees() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT employee_id, name FROM employees");
             ResultSet rs = ps.executeQuery()) {
            employeeBox.addItem("All");
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

    private void loadData() {
        model.setRowCount(0);

        String empLabel = (String) employeeBox.getSelectedItem();
        String empId = employeeMap.get(empLabel);
        String month = (String) monthBox.getSelectedItem();
        String year = (String) yearBox.getSelectedItem();

        StringBuilder query = new StringBuilder("SELECT * FROM employee_payments WHERE 1=1");
        if (!"All".equals(empLabel)) query.append(" AND employee_id = '").append(empId).append("'");
        if (!"All".equals(month)) query.append(" AND month = '").append(month).append("'");
        if (!"All".equals(year)) query.append(" AND year = ").append(year);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("month"),
                    rs.getInt("year"),
                    rs.getDouble("amount"),
                    rs.getDate("payment_date"),
                    rs.getString("remarks")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No records found for selected filters.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new EmployeePaymentHistory(null);
    }
}
