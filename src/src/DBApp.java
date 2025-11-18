import view.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class DBApp {
    private JFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DBApp(String userName) {
        initializeGUI(userName);
    }

    private void initializeGUI(String userName) {
        mainFrame = new JFrame("Barangay Health Monitoring System - Welcome, " + userName);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1400, 900);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 5 core record management panels
        contentPanel.add(new DashboardPanel(userName, cardLayout, contentPanel), "DASHBOARD");
        contentPanel.add(createPatientRecordsPanel(), "PATIENTS");
        contentPanel.add(createMedicineInventoryPanel(), "MEDICINE");
        contentPanel.add(createHealthWorkerRecordsPanel(), "WORKERS");
        contentPanel.add(createFacilityRecordsPanel(), "FACILITIES");
        contentPanel.add(createSupplierRecordsPanel(), "SUPPLIERS");

        JPanel sidebar = createSidebar();

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(sidebar, BorderLayout.WEST);
        mainFrame.add(contentPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private JPanel createPatientRecordsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Patient Records - Under Construction"));
        return panel;
    }

    private JPanel createMedicineInventoryPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Medicine Inventory - Under Construction"));
        return panel;
    }

    private JPanel createHealthWorkerRecordsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Health Worker Records - Under Construction"));
        return panel;
    }

    private JPanel createFacilityRecordsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Facility Records - Under Construction"));
        return panel;
    }

    private JPanel createSupplierRecordsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Supplier Records - Under Construction"));
        return panel;
    }

    private JPanel createSidebar() {
    JPanel sidebar = new JPanel();
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBackground(new Color(70, 130, 180));
    sidebar.setPreferredSize(new Dimension(250, 0));
    sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    // core records menu buttons
    String[] menuItems = {
        "Dashboard", 
        "Patient Records", 
        "Medicine Inventory", 
        "Health Worker Records",
        "Facility Records", 
        "Supplier Records"
    };

    String[] cardNames = {
        "DASHBOARD", 
        "PATIENTS", 
        "MEDICINE", 
        "WORKERS",
        "FACILITIES", 
        "SUPPLIERS"
    };

    for (int i = 0; i < menuItems.length; i++) {
        JButton menuButton = createMenuButton(menuItems[i], cardNames[i]);
        sidebar.add(menuButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    // logout button at the bottom
    sidebar.add(Box.createVerticalGlue());
        JButton logoutButton = createMenuButton("Logout", "LOGOUT");
        logoutButton.setBackground(new Color(220, 80, 60));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                "Are you sure you want to logout?", "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                System.exit(0); 
            }
        });
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userName = JOptionPane.showInputDialog("Enter your name:");
            if (userName != null && !userName.trim().isEmpty()) {
                new DBApp(userName);
            } else {
                System.exit(0);
            }
        });
    }
}