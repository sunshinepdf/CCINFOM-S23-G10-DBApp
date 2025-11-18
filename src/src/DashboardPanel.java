import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        contentArea.add(createAlertsPanel());

        return contentArea;
    }

     private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent System Activity"));
        panel.setBackground(Color.WHITE);

        String[] activities = {
            "gusto nyo ba to kasama"
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
    
    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Alerts & Notifications"));
        panel.setBackground(Color.WHITE);

        DefaultListModel<String> alertsModel = new DefaultListModel<>();
        alertsModel.addElement("🔴 red");
        alertsModel.addElement("🟡 orange");
        alertsModel.addElement("🟢 green");

        JList<String> alertsList = new JList<>(alertsModel);
        alertsList.setBackground(new Color(255, 250, 250));
        alertsList.setFont(new Font("Arial", Font.PLAIN, 12));
        
        alertsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = value.toString();
                if (text.contains("🔴")) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (text.contains("🟡")) {
                    c.setForeground(Color.ORANGE);
                } else if (text.contains("🟢")) {
                    c.setForeground(Color.GREEN);
                }
                return c;
            }
        });

        panel.add(new JScrollPane(alertsList), BorderLayout.CENTER);
        return panel;
    }
}
