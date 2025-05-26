package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;
import hostelbuddy.utils.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class WorkingEmployees extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public WorkingEmployees(JFrame parent) {
        setTitle("Working Employees");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 255, 250));

        JLabel heading = new JLabel("Employees Currently Working");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBounds(300, 20, 400, 30);
        add(heading);

        JLabel searchLabel = new JLabel("Search (ID/Name):");
        searchLabel.setBounds(50, 70, 120, 25);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(170, 70, 200, 25);
        add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(380, 70, 100, 25);
        add(searchBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(490, 70, 100, 25);
        add(refreshBtn);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBounds(600, 70, 140, 25);
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(850, 20, 100, 25);
        add(backBtn);

        String[] columns = {
            "Employee ID", "Name", "Gender", "Mobile", "Email",
            "Aadhaar", "Address", "Role", "Joining Date", "Status"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 120, 880, 400);
        add(scrollPane);

        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
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

        loadData();
        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0);
        String query = "SELECT * FROM employees WHERE status = 'Working'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("mobile_number"),
                    rs.getString("email"),
                    rs.getString("aadhaar_number"),
                    rs.getString("address"),
                    rs.getString("role"),
                    rs.getString("joining_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ID or Name.");
            return;
        }

        model.setRowCount(0);
        String query = "SELECT * FROM employees WHERE status = 'Working' AND (employee_id LIKE ? OR name LIKE ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("mobile_number"),
                    rs.getString("email"),
                    rs.getString("aadhaar_number"),
                    rs.getString("address"),
                    rs.getString("role"),
                    rs.getString("joining_date"),
                    rs.getString("status")
                });
            }
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No matching records.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new WorkingEmployees(null);
    }
}
