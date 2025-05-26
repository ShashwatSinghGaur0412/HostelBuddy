package hostelbuddy.utils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;

public class ExportUtil {

    public static void exportTableToCSV(JTable table, String filePath) {
        try (FileWriter csv = new FileWriter(filePath)) {
            TableModel model = table.getModel();

            // Write header
            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) csv.write(",");
            }
            csv.write("\n");

            // Write data
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    csv.write(String.valueOf(model.getValueAt(row, col)));
                    if (col < model.getColumnCount() - 1) csv.write(",");
                }
                csv.write("\n");
            }

            JOptionPane.showMessageDialog(null, "CSV exported successfully to: " + filePath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Export failed: " + e.getMessage());
        }
    }
}
