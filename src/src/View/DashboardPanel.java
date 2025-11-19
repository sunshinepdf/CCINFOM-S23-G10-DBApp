package View;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Model.DBConnection;
import Model.ViewDAO;

public class DashboardPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public DashboardPanel(String userName, CardLayout cardLayout, JPanel contentPanel) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;
        initializeDashboard(userName);
    }

    private void initializeDashboard(String userName) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // 1. header
        JPanel headerPanel = createHeaderPanel(userName);
        
        // 2. statistics
        JPanel statsPanel = createStatsPanel();
        
        // 3. content area
        JPanel contentArea = createContentArea();

        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(contentArea, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel(String userName) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel welcomeLabel = new JLabel("Barangay Health Monitoring System", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(70, 130, 180));

        JLabel userLabel = new JLabel("Welcome, " + userName + " | " + java.time.LocalDate.now());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.GRAY);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        statsPanel.setBackground(Color.WHITE);

        statsPanel.add(createStatCard("Total Patients", "number", Color.BLUE, "[P]", "PATIENTS"));
        statsPanel.add(createStatCard("Medicine Inventory", "number%", Color.GREEN, "[M]", "MEDICINE"));
        statsPanel.add(createStatCard("Active Health Workers", "number", Color.ORANGE, "[H]", "WORKERS"));
        statsPanel.add(createStatCard("Facilities", "number", Color.MAGENTA, "[F]", "FACILITIES"));
        statsPanel.add(createStatCard("Suppliers", "number", Color.CYAN, "[S]", "SUPPLIERS"));
        statsPanel.add(createStatCard("Low Stock Items", "number", Color.RED, "[!]", "MEDICINE"));

        // Transaction quick-links are available in the sidebar; removed from dashboard cards

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color, String emoji, String cardTarget) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 46));
        valueLabel.setForeground(color);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(emojiLabel, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, cardTarget);
            }
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new GridLayout(1, 2, 20, 0));
        contentArea.setBackground(Color.WHITE);

        contentArea.add(createActivityPanel());
        contentArea.add(createReportsPanel());

        return contentArea;
    }

     private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent System Activity"));
        panel.setBackground(Color.WHITE);

        String[] activities = {
            "No updates"
        };

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String activity : activities) {
            listModel.addElement(activity);
        }

        JList<String> activityList = new JList<>(listModel);
        activityList.setBackground(Color.WHITE);
        activityList.setFont(new Font("Arial", Font.PLAIN, 12));
        activityList.setEnabled(false);

        panel.add(new JScrollPane(activityList), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reports"));
        panel.setBackground(Color.WHITE);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 8, 8));

        // Consultation summary buttons
        JButton consWeek = new JButton("Consultation Summary - Week");
        JButton consMonth = new JButton("Consultation Summary - Month");
        JButton consYear = new JButton("Consultation Summary - Year");

        // Immunization impact
        JButton immWeek = new JButton("Immunization Impact - Week");
        JButton immMonth = new JButton("Immunization Impact - Month");
        JButton immYear = new JButton("Immunization Impact - Year");

        // Medicine inventory utilization
        JButton utilWeek = new JButton("Medicine Inventory Utilization - Week");
        JButton utilMonth = new JButton("Medicine Inventory Utilization - Month");
        JButton utilYear = new JButton("Medicine Inventory Utilization - Year");

        // Disease monitoring
        JButton disWeek = new JButton("Disease Case Monitoring - Week");
        JButton disMonth = new JButton("Disease Case Monitoring - Month");
        JButton disYear = new JButton("Disease Case Monitoring - Year");

        buttons.add(consWeek);
        buttons.add(consMonth);
        buttons.add(consYear);
        buttons.add(immWeek);
        buttons.add(immMonth);
        buttons.add(immYear);
        buttons.add(utilWeek);
        buttons.add(utilMonth);
        buttons.add(utilYear);
        buttons.add(disWeek);
        buttons.add(disMonth);
        buttons.add(disYear);

        panel.add(buttons, BorderLayout.CENTER);

        // action helpers
        consWeek.addActionListener(e -> fetchAndShow(v -> v.getConsultationSummaryWeek(), "Consultation Summary - Week"));
        consMonth.addActionListener(e -> fetchAndShow(v -> v.getConsultationSummaryMonth(), "Consultation Summary - Month"));
        consYear.addActionListener(e -> fetchAndShow(v -> v.getConsultationSummaryYear(), "Consultation Summary - Year"));

        immWeek.addActionListener(e -> fetchAndShow(v -> v.getImmunizationImpactWeek(), "Immunization Impact - Week"));
        immMonth.addActionListener(e -> fetchAndShow(v -> v.getImmunizationImpactMonth(), "Immunization Impact - Month"));
        immYear.addActionListener(e -> fetchAndShow(v -> v.getImmunizationImpactYear(), "Immunization Impact - Year"));

        utilWeek.addActionListener(e -> fetchAndShow(v -> v.getMedicineInventoryUtilizationWeek(), "Medicine Inventory Utilization - Week"));
        utilMonth.addActionListener(e -> fetchAndShow(v -> v.getMedicineInventoryUtilizationMonth(), "Medicine Inventory Utilization - Month"));
        utilYear.addActionListener(e -> fetchAndShow(v -> v.getMedicineInventoryUtilizationYear(), "Medicine Inventory Utilization - Year"));

        disWeek.addActionListener(e -> fetchAndShow(v -> v.getDiseaseCaseMonitoringWeek(), "Disease Case Monitoring - Week"));
        disMonth.addActionListener(e -> fetchAndShow(v -> v.getDiseaseCaseMonitoringMonth(), "Disease Case Monitoring - Month"));
        disYear.addActionListener(e -> fetchAndShow(v -> v.getDiseaseCaseMonitoringYear(), "Disease Case Monitoring - Year"));

        return panel;
    }

    private interface ViewFetcher {
        List<Map<String, Object>> fetch(ViewDAO dao) throws Exception;
    }

    private void fetchAndShow(ViewFetcher fetcher, String title) {
        // Run DB call off-EDT
        new Thread(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                ViewDAO dao = new ViewDAO(conn);
                List<Map<String, Object>> rows = fetcher.fetch(dao);
                SwingUtilities.invokeLater(() -> View.ViewDialog.showView(this, title, rows));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error loading report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}
