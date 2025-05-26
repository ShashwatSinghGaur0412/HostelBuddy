package hostelbuddy.ui;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Login - HostelBuddy");
        setSize(1920, 1080); // Full HD
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        int width = getWidth();
        int height = getHeight();

        int wallpaperPanelWidth = (int) (width * 0.75);  // 75%
        int loginPanelWidth = width - wallpaperPanelWidth; // 25%

        // ==== Wallpaper Panel ====
        JLabel wallpaperLabel = new JLabel();
        wallpaperLabel.setBounds(0, 0, wallpaperPanelWidth, height);

        try {
            ImageIcon wallpaper = new ImageIcon(getClass().getResource("/resources/LoginScreenBG.jpg"));
            Image img = wallpaper.getImage().getScaledInstance(wallpaperPanelWidth, height, Image.SCALE_SMOOTH);
            wallpaperLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            wallpaperLabel.setText("Wallpaper not found");
            wallpaperLabel.setHorizontalAlignment(SwingConstants.CENTER);
            wallpaperLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        }

        add(wallpaperLabel);

        // ==== Login Panel ====
        JPanel loginPanel = new JPanel(null);
        loginPanel.setBounds(wallpaperPanelWidth, 0, loginPanelWidth, height);
        loginPanel.setBackground(new Color(33, 33, 33));
        add(loginPanel);

        int fieldWidth = loginPanelWidth - 60;
        int xCenter = (loginPanelWidth - fieldWidth) / 2;

        // ==== Center Container for Login Fields ====
        JPanel centerPanel = new JPanel(null);
        centerPanel.setOpaque(false);
        int centerPanelHeight = 420; // More height for extra spacing
        centerPanel.setBounds(0, (height - centerPanelHeight) / 2, loginPanelWidth, centerPanelHeight);
        loginPanel.add(centerPanel);

        int y = 0;
        int gap = 30; // More vertical space

        JLabel title = new JLabel("Welcome to HostelBuddy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBounds(xCenter, y, fieldWidth, 40);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(title);

        y += 40 + gap;

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setBounds(xCenter, y, fieldWidth, 30);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(userLabel);

        y += 30 + 10;

        JTextField userField = new JTextField("admin");
        userField.setBounds(xCenter, y, fieldWidth, 45);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        centerPanel.add(userField);

        y += 45 + gap;

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        passLabel.setForeground(Color.LIGHT_GRAY);
        passLabel.setBounds(xCenter, y, fieldWidth, 30);
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(passLabel);

        y += 30 + 10;

        JPasswordField passField = new JPasswordField("admin");
        passField.setBounds(xCenter, y, fieldWidth, 45);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        centerPanel.add(passField);

        y += 45 + gap;

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginBtn.setBackground(new Color(0, 120, 215));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBounds(xCenter, y, fieldWidth, 50);
        centerPanel.add(loginBtn);

        // ==== Button Action ====
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("admin")) {
                dispose();
                new HomePage(); // Replace with your HomePage class
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}
