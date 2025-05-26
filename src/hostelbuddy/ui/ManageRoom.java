package hostelbuddy.ui;

import hostelbuddy.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageRoom extends JFrame {
    private JTable roomTable;
    private DefaultTableModel model;
    private JTextField roomField, updateRoomField;
    private JCheckBox activateCheckbox, updateActivateCheckbox;

    public ManageRoom() {
        setTitle("Manage Room - HostelBuddy");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        int leftWidth = (int) (getWidth() * 0.4);
        int rightWidth = getWidth() - leftWidth;
        int fullHeight = getHeight();

        JPanel leftPanel = new JPanel(null);
        leftPanel.setBounds(0, 0, leftWidth, fullHeight);
        leftPanel.setBackground(new Color(230, 201, 168));
        add(leftPanel);

        int halfHeight = fullHeight / 2;

        JLabel addTitle = new JLabel("Add New Room", SwingConstants.CENTER);
        addTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        addTitle.setBounds(0, 20, leftWidth, 30);
        leftPanel.add(addTitle);

        JLabel roomLabel = new JLabel("Room Number:");
        roomLabel.setBounds(30, 80, 120, 25);
        leftPanel.add(roomLabel);

        roomField = new JTextField();
        roomField.setBounds(160, 80, 180, 25);
        leftPanel.add(roomField);

        activateCheckbox = new JCheckBox("Activate");
        activateCheckbox.setBounds(leftWidth - 160, 120, 100, 25);
        leftPanel.add(activateCheckbox);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(leftWidth - 160, 160, 100, 30);
        leftPanel.add(saveBtn);

        saveBtn.addActionListener(e -> saveRoom());

        JSeparator separator = new JSeparator();
        separator.setBounds(20, halfHeight - 10, leftWidth - 40, 1);
        leftPanel.add(separator);

        int startY = halfHeight + 10;

        JLabel updateTitle = new JLabel("Update/Delete Room", SwingConstants.CENTER);
        updateTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        updateTitle.setBounds(0, startY, leftWidth, 30);
        leftPanel.add(updateTitle);

        JLabel updateRoomLabel = new JLabel("Room Number:");
        updateRoomLabel.setBounds(30, startY + 50, 120, 25);
        leftPanel.add(updateRoomLabel);

        updateRoomField = new JTextField();
        updateRoomField.setBounds(160, startY + 50, 180, 25);
        leftPanel.add(updateRoomField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(leftWidth - 160, startY + 90, 100, 30);
        leftPanel.add(searchBtn);

        searchBtn.addActionListener(e -> searchRoom());

        updateActivateCheckbox = new JCheckBox("Activate");
        updateActivateCheckbox.setBounds(leftWidth - 160, startY + 130, 100, 25);
        leftPanel.add(updateActivateCheckbox);

        JButton updateBtn = new JButton("Update");
        updateBtn.setBounds(leftWidth - 270, startY + 170, 100, 30);
        leftPanel.add(updateBtn);

        updateBtn.addActionListener(e -> updateRoom());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(leftWidth - 160, startY + 170, 100, 30);
        leftPanel.add(deleteBtn);

        deleteBtn.addActionListener(e -> deleteRoom());

        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(leftWidth, 0, rightWidth, fullHeight);
        rightPanel.setBackground(new Color(253, 245, 230));
        add(rightPanel);

        JLabel allRoomTitle = new JLabel("All Room");
        allRoomTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        allRoomTitle.setBounds(30, 20, 200, 30);
        rightPanel.add(allRoomTitle);

        JButton backBtn = new JButton("Back to Home");
        backBtn.setBounds(rightWidth - 150, 20, 120, 30);
        rightPanel.add(backBtn);
        backBtn.addActionListener(e -> dispose());

        String[] columns = {"Room Number", "Activate", "Room Status"};
        model = new DefaultTableModel(columns, 0);
        roomTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(30, 70, rightWidth - 60, fullHeight - 150);
        rightPanel.add(scrollPane);

        loadAllRooms();
        setVisible(true);
        backBtn.addActionListener(e -> dispose());
    }

    private void loadAllRooms() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM rooms");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String number = rs.getString("room_number");
                String activate = rs.getBoolean("activate") ? "Yes" : "No";
                String status = rs.getString("status");
                model.addRow(new Object[]{number, activate, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + e.getMessage());
        }
    }

    private void saveRoom() {
        String roomNumber = roomField.getText().trim();
        boolean isActive = activateCheckbox.isSelected();

        if (roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room Number is required");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement checkStmt = con.prepareStatement("SELECT * FROM rooms WHERE room_number = ?");
            checkStmt.setString(1, roomNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Room already exists!");
                return;
            }

            PreparedStatement stmt = con.prepareStatement("INSERT INTO rooms (room_number, activate, status) VALUES (?, ?, ?)");
            stmt.setString(1, roomNumber);
            stmt.setBoolean(2, isActive);
            stmt.setString(3, "Not Booked");
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room added successfully");
            roomField.setText("");
            activateCheckbox.setSelected(false);
            loadAllRooms();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchRoom() {
        String room = updateRoomField.getText().trim();
        if (room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Room Number to search");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM rooms WHERE room_number = ?");
            stmt.setString(1, room);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                updateActivateCheckbox.setSelected(rs.getBoolean("activate"));
            } else {
                JOptionPane.showMessageDialog(this, "Room not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateRoom() {
        String room = updateRoomField.getText().trim();
        boolean isActive = updateActivateCheckbox.isSelected();

        if (room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room Number is required");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("UPDATE rooms SET activate = ? WHERE room_number = ?");
            stmt.setBoolean(1, isActive);
            stmt.setString(2, room);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Room updated");
                loadAllRooms();
            } else {
                JOptionPane.showMessageDialog(this, "Room not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteRoom() {
        String room = updateRoomField.getText().trim();
        if (room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Room Number to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this room?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM rooms WHERE room_number = ?");
            stmt.setString(1, room);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Room deleted");
                updateRoomField.setText("");
                updateActivateCheckbox.setSelected(false);
                loadAllRooms();
            } else {
                JOptionPane.showMessageDialog(this, "Room not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ManageRoom();
    }
}