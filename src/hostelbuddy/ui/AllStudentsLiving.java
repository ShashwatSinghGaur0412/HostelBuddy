package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;
import hostelbuddy.utils.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AllStudentsLiving extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public AllStudentsLiving(JFrame parent) {
        setTitle("All Students Living");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(new Color(250, 255, 240));

        JLabel title = new JLabel("All Students Currently Living");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(320, 20, 400, 30);
        add(title);

        // Search bar
        JLabel searchLabel = new JLabel("Search (ID or Name):");
        searchLabel.setBounds(50, 70, 150, 25);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(200, 70, 200, 25);
        add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(420, 70, 100, 25);
        add(searchBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBounds(530, 70, 100, 25);
        add(resetBtn);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBounds(640, 70, 140, 25);
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(840, 20, 100, 25);
        add(backBtn);

        String[] columns = {
            "Student ID", "Name", "Mobile", "Email",
            "Father's Name", "Mother's Name", "Address",
            "College", "Aadhaar", "Room"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 120, 940, 400);
        add(scrollPane);

        // Actions
        searchBtn.addActionListener(e -> searchStudents());
        resetBtn.addActionListener(e -> loadData());
        exportBtn.addActionListener(e -> exportCSV());
        backBtn.addActionListener(e -> {
            dispose();
            parent.setEnabled(true);
            parent.toFront();
        });

        loadData(); // initial table load
        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0); // clear
        String query = "SELECT * FROM students";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("mobile_number"),
                    rs.getString("email"),
                    rs.getString("father_name"),
                    rs.getString("mother_name"),
                    rs.getString("address"),
                    rs.getString("college_name"),
                    rs.getString("aadhaar_number"),
                    rs.getString("room_number")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void searchStudents() {
        model.setRowCount(0);
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a name or ID to search.");
            return;
        }

        String query = "SELECT * FROM students WHERE student_id LIKE ? OR name LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("mobile_number"),
                    rs.getString("email"),
                    rs.getString("father_name"),
                    rs.getString("mother_name"),
                    rs.getString("address"),
                    rs.getString("college_name"),
                    rs.getString("aadhaar_number"),
                    rs.getString("room_number")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No matching records found.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
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
        new AllStudentsLiving(null);
    }
}
