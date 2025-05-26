package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;
import hostelbuddy.utils.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LeavedStudents extends JFrame {
    private DefaultTableModel model;
    private JTable table;

    public LeavedStudents(JFrame parent) {
        setTitle("Leaved Students");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(new Color(255, 245, 240));

        JLabel title = new JLabel("Students Who Have Left the Hostel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(250, 20, 500, 30);
        add(title);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(50, 70, 120, 25);
        add(refreshBtn);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBounds(180, 70, 140, 25);
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(850, 20, 100, 25);
        add(backBtn);

        String[] columns = {
            "Student ID", "Name", "Email", "Room", "Left On (Aadhaar)", "Mobile", "Status"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 120, 880, 400);
        add(scrollPane);

        refreshBtn.addActionListener(e -> loadData());
        exportBtn.addActionListener(e -> exportCSV());
        backBtn.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        loadData();
        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0);
        String query = "SELECT * FROM students WHERE status = 'Leaved'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("room_number"),
                    rs.getString("aadhaar_number"),
                    rs.getString("mobile_number"),
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            ExportUtil.exportTableToCSV(table, path);
        }
    }

    public static void main(String[] args) {
        new LeavedStudents(null);
    }
}
