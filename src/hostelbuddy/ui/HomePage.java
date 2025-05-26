package hostelbuddy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Home - HostelBuddy");
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // === Wallpaper as background ===
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/resources/LoginScreenBG.jpg")));
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        setContentPane(background);

        // === Title: HostelBuddy ===
        JLabel title = new JLabel("HostelBuddy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 10, 0));

        // === Subtitle: Home Menu ===
        JLabel subtitle = new JLabel("Home Menu");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // === Icon-style Panel with GridLayout 5 per row ===
        JPanel iconPanel = new JPanel(new GridLayout(0, 5, 40, 30));
        iconPanel.setOpaque(false);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        Object[][] cards = {
                {"Manage Room", "/resources/icons/room.png", (Runnable) () -> open(new ManageRoom())},
                {"New Student", "/resources/icons/student_add.png", (Runnable) () -> open(new NewStudent(this))},
                {"Update Student", "/resources/icons/student_update.png", (Runnable) () -> open(new UpdateDeleteStudent(this))},
                {"Student Fees", "/resources/icons/fees.png", (Runnable) () -> open(new StudentFees(this))},
                {"Fee History", "/resources/icons/history.png", (Runnable) () -> open(new FeeHistoryViewer(this))},
                {"Monthly Report", "/resources/icons/report.png", (Runnable) () -> open(new MonthlyFeeReport(this))},
                {"All Students", "/resources/icons/living.png", (Runnable) () -> open(new AllStudentsLiving(this))},
                {"Leaved Students", "/resources/icons/leaved.png", (Runnable) () -> open(new LeavedStudents(this))},
                {"New Employee", "/resources/icons/employee_add.png", (Runnable) () -> open(new NewEmployee(this))},
                {"Update Employee", "/resources/icons/employee_update.png", (Runnable) () -> open(new UpdateDeleteEmployee(this))},
                {"Working Employees", "/resources/icons/working.png", (Runnable) () -> open(new WorkingEmployees(this))},
                {"Leaved Employees", "/resources/icons/leaved.png", (Runnable) () -> open(new LeavedEmployees(this))},
                {"Employee Payment", "/resources/icons/payment.png", (Runnable) () -> open(new EmployeePayment(this))},
                {"Payment History", "/resources/icons/history.png", (Runnable) () -> open(new EmployeePaymentHistory(this))}
        };

        for (Object[] data : cards) {
            iconPanel.add(createIconCard((String) data[0], (String) data[1], (Runnable) data[2]));
        }

        JScrollPane scrollPane = new JScrollPane(iconPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Logout Button ===
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoutBtn.setBackground(new Color(200, 0, 0));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginScreen();
            }
        });

        // === Add to background ===
        background.add(title);
        background.add(subtitle);
        background.add(scrollPane);
        background.add(Box.createRigidArea(new Dimension(0, 30)));
        background.add(logoutBtn);
        background.add(Box.createRigidArea(new Dimension(0, 50)));

        setVisible(true);
    }

    private JPanel createIconCard(String title, String iconPath, Runnable onClick) {
        JPanel outerBox = new JPanel();
        outerBox.setLayout(new BorderLayout());
        outerBox.setOpaque(false);
        outerBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(100, 100));

        JLabel iconLabel;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            iconLabel = new JLabel("❓");
        }
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><center>" + title + "</center></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        card.add(iconLabel);
        card.add(titleLabel);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });

        outerBox.add(card, BorderLayout.CENTER);
        return outerBox;
    }

    private void open(JFrame frame) {
        this.setEnabled(false);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                setEnabled(true);
                toFront();
            }

            public void windowClosed(java.awt.event.WindowEvent e) {
                setEnabled(true);
                toFront();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomePage::new);
    }
}