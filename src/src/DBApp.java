import View.*;

import javax.swing.*;
import java.awt.*;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.sql.SQLException;

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

        // core record management panels
        contentPanel.add(new DashboardPanel(userName, cardLayout, contentPanel), "DASHBOARD");
        contentPanel.add(new PatientPanel(), "PATIENTS");
        contentPanel.add(new MedicineInventoryPanel(), "MEDICINE");
        contentPanel.add(new HealthWorkerPanel(), "WORKERS");
        contentPanel.add(new FacilityPanel(), "FACILITIES");
        contentPanel.add(new SupplierPanel(), "SUPPLIERS");

        // transaction panels
        contentPanel.add(new PrescriptionPanel(), "PRESCRIPTIONS");
        contentPanel.add(new RestockInvoicePanel(), "RESTOCK");
        contentPanel.add(new ImmunizationAdministrationPanel(), "IMMUNIZATION");
        contentPanel.add(new ConsultationPanel(), "CONSULTATIONS");

        JPanel sidebar = createSidebar();

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(sidebar, BorderLayout.WEST);
        mainFrame.add(contentPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private JPanel createSidebar() {
    JPanel sidebar = new JPanel();
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBackground(new Color(70, 130, 180));
    sidebar.setPreferredSize(new Dimension(250, 0));
    sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    // core records + transactions menu buttons
    String[] menuItems = {
        "Dashboard",
        "Patient Records",
        "Medicine Inventory",
        "Health Worker Records",
        "Facility Records",
        "Supplier Records",
        "Prescription Transactions",
        "Restock Invoices",
        "Immunization Administration",
        "Consultation Transactions"
    };

    String[] cardNames = {
        "DASHBOARD",
        "PATIENTS",
        "MEDICINE",
        "WORKERS",
        "FACILITIES",
        "SUPPLIERS",
        "PRESCRIPTIONS",
        "RESTOCK",
        "IMMUNIZATION",
        "CONSULTATIONS"
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
        // Install global exception handlers so SQL errors or other uncaught
        // exceptions show a UI popup instead of terminating the JVM.
        installGlobalExceptionHandler();

        SwingUtilities.invokeLater(() -> {
            String userName = JOptionPane.showInputDialog("Enter your name:");
            if (userName != null && !userName.trim().isEmpty()) {
                new DBApp(userName);
            } else {
                System.exit(0);
            }
        });
    }

    private static void installGlobalExceptionHandler() {
        // Handler for non-EDT threads
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            handleUncaught(throwable);
        });

        // Handler for the Event Dispatch Thread (EDT)
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                try {
                    super.dispatchEvent(event);
                } catch (Throwable t) {
                    handleUncaught(t);
                }
            }
        });
    }

    private static void handleUncaught(Throwable t) {
        // Log to console for developers
        t.printStackTrace();

        // Determine friendly message for SQL errors
        String title = "Application Error";
        String message;
        if (t instanceof SQLException) {
            message = "A database error occurred:\n" + t.getMessage();
        } else if (t.getCause() instanceof SQLException) {
            message = "A database error occurred:\n" + t.getCause().getMessage();
        } else {
            message = "An unexpected error occurred:\n" + t.getClass().getSimpleName() + ": " + t.getMessage();
        }

        // Show the dialog on the EDT
        try {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE));
        } catch (Throwable ignore) {
            // If even the UI is unusable, print to console as a last resort
            System.err.println(message);
        }
    }
}