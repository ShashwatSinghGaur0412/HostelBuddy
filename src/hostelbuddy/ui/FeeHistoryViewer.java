package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;
import hostelbuddy.utils.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FeeHistoryViewer extends JFrame {
    private JTextField studentIdField;
    private JComboBox<String> monthDropdown;
    private DefaultTableModel model;
    private JTable table;

    public FeeHistoryViewer(JFrame parent) {
        setTitle("Fee History Viewer");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(new Color(255, 250, 240));

        JLabel title = new JLabel("Fee History Viewer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(320, 20, 400, 30);
        add(title);

        JLabel studentLabel = new JLabel("Student ID:");
        studentLabel.setBounds(50, 80, 100, 25);
        add(studentLabel);

        studentIdField = new JTextField();
        studentIdField.setBounds(150, 80, 150, 25);
        add(studentIdField);

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setBounds(320, 80, 60, 25);
        add(monthLabel);

        monthDropdown = new JComboBox<>(new String[] {
            "All", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthDropdown.setBounds(380, 80, 150, 25);
        add(monthDropdown);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(550, 80, 100, 25);
        add(searchBtn);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBounds(660, 80, 150, 25);
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(820, 20, 100, 25);
        add(backBtn);

        searchBtn.addActionListener(e -> fetchRecords());

        exportBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV");
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".csv")) {
                    path += ".csv";
                }
                ExportUtil.exportTableToCSV(table, path);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        // Table Setup
        String[] columns = { "Student ID", "Name", "Email", "Room", "Month", "Amount" };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 130, 850, 400);
        add(scrollPane);

        setVisible(true);
    }

    private void fetchRecords() {
        model.setRowCount(0); // clear table

        String studentId = studentIdField.getText().trim();
        String month = monthDropdown.getSelectedItem().toString();

        String query = "SELECT * FROM fees WHERE 1=1";
        if (!studentId.isEmpty()) query += " AND student_id = ?";
        if (!month.equals("All")) query += " AND month = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            int paramIndex = 1;
            if (!studentId.isEmpty()) stmt.setString(paramIndex++, studentId);
            if (!month.equals("All")) stmt.setString(paramIndex, month);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("room_number"),
                    rs.getString("month"),
                    rs.getString("amount")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No records found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new FeeHistoryViewer(null);
    }
}
