
package hostelbuddy.ui;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JFrame {

    private JProgressBar progressBar;
    private JLabel percentLabel;

    public WelcomeScreen() {
        // Basic frame settings
        setTitle("HostelBuddy - Loading");
        setSize(600, 350);
        setLocationRelativeTo(null); // Center screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true); // Borderless sleek splash screen

        // Initialize the UI
        initializeUI();

        // Start the loading animation
        startLoading();
    }

    /**
     * Initializes the layered UI including background and foreground components.
     */
    private void initializeUI() {
        // LayeredPane to allow stacking components (background behind, foreground above)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(600, 350));

        // ===== Background Image =====
        ImageIcon bgImage = new ImageIcon("src/resources/WelcomeScreenBG.jpg"); // Ensure correct path
        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0, 600, 350); // Full size
        layeredPane.add(background, Integer.valueOf(0)); // Add background at bottom layer

        // ===== Foreground Panel with transparent background =====
        JPanel contentPanel = new JPanel(null); // Use absolute positioning
        contentPanel.setOpaque(false); // See-through to background
        contentPanel.setBounds(0, 0, 600, 350);

        // ==== Title Label ====
        JLabel title = new JLabel("Welcome to HostelBuddy");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBounds(120, 60, 400, 30);
        contentPanel.add(title);

        // ==== Slogan Label ====
        JLabel slogan = new JLabel("Your Hostel, Simplified.");
        slogan.setForeground(new Color(0, 128, 128));
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        slogan.setBounds(200, 100, 300, 20);
        contentPanel.add(slogan);

        // ==== Progress Bar ====
        progressBar = new JProgressBar();
        progressBar.setBounds(100, 220, 400, 25);
        progressBar.setForeground(new Color(100, 200, 255)); // Light blue fill
        progressBar.setBackground(new Color(60, 60, 90));     // Dark base
        progressBar.setBorderPainted(false);                  // Cleaner look
        progressBar.setStringPainted(false);                  // We show % separately
        contentPanel.add(progressBar);

        // ==== Percentage Label ====
        percentLabel = new JLabel("0%", SwingConstants.CENTER);
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        percentLabel.setForeground(Color.WHITE);
        percentLabel.setBounds(275, 250, 50, 30);
        contentPanel.add(percentLabel);

        // Add content panel above background
        layeredPane.add(contentPanel, Integer.valueOf(1));

        // Set the layered pane as the content of the frame
        setContentPane(layeredPane);
    }

    /**
     * Starts a simple progress simulation and launches LoginScreen after loading completes.
     */
    private void startLoading() {
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(30); // Control animation speed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setValue(i);
                percentLabel.setText(i + "%");
            }

            // Close splash screen and open login
            dispose();
            new LoginScreen().setVisible(true);
        }).start();
    }

    /**
     * Main method to launch the welcome screen.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen ws = new WelcomeScreen();
            ws.setVisible(true);
        });
    }
}
