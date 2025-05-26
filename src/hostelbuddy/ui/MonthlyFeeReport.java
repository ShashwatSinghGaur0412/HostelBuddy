package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;
import hostelbuddy.utils.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MonthlyFeeReport extends JFrame {
    private DefaultTableModel model;
    private JTable table;

    public MonthlyFeeReport(JFrame parent) {
        setTitle("Monthly Fee Report");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel("Monthly Fee Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(220, 20, 300, 30);
        add(title);

        JButton loadBtn = new JButton("Load Report");
        loadBtn.setBounds(50, 70, 140, 30);
        add(loadBtn);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBounds(220, 70, 140, 30);
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 70, 100, 30);
        add(backBtn);

        String[] columns = { "Month", "Number of Payments", "Total Amount Collected" };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 120, 580, 300);
        add(scrollPane);

        loadBtn.addActionListener(e -> loadReport());

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

        setVisible(true);
    }

    private void loadReport() {
        model.setRowCount(0);

        String query = "SELECT month, COUNT(*) as count, SUM(amount) as total FROM fees GROUP BY month ORDER BY FIELD(month, " +
                "'January','February','March','April','May','June','July','August','September','October','November','December')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("month"),
                    rs.getInt("count"),
                    rs.getDouble("total")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data available for report.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading report: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new MonthlyFeeReport(null);
    }
}
