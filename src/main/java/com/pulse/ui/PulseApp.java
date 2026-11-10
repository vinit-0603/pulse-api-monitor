package com.pulse.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.pulse.database.DatabaseManager;
import com.pulse.service.MonitoringService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PulseApp extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton dashboardBtn, endpointsBtn, analyticsBtn, historyBtn, settingsBtn;

    public PulseApp() {
        setTitle("PULSE — API Failure Detection & Monitoring System");
        setSize(1440, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Database
        DatabaseManager.initializeDatabase();

        // Start Background Monitoring
        MonitoringService.startMonitoring();

        // Main Layout Structure
        setLayout(new BorderLayout());

        // Create Sidebar Navigation
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Create Content Area with CardLayout inside a Dark Gradient Panel
        cardLayout = new CardLayout();
        
        // Dark Gradient: Deep Obsidian (#080A10) -> Midnight Purple Slate (#12182B)
        Color darkColorStart = new Color(8, 10, 16);
        Color darkColorEnd = new Color(18, 24, 43);

        contentPanel = new GradientPanel(darkColorStart, darkColorEnd, cardLayout);
        contentPanel.setOpaque(false);
        
        // Add All Application Panels
        DashboardPanel dashboardPanel = new DashboardPanel();
        EndpointsPanel endpointsPanel = new EndpointsPanel();
        HistoryPanel historyPanel = new HistoryPanel();
        AnalyticsPanel analyticsPanel = new AnalyticsPanel();
        SettingsPanel settingsPanel = new SettingsPanel();

        // Make child panel backgrounds transparent to let gradient show through
        makePanelTransparent(dashboardPanel);
        makePanelTransparent(endpointsPanel);
        makePanelTransparent(historyPanel);
        makePanelTransparent(analyticsPanel);
        makePanelTransparent(settingsPanel);

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(endpointsPanel, "Endpoints");
        contentPanel.add(historyPanel, "History");
        contentPanel.add(analyticsPanel, "Analytics");
        contentPanel.add(settingsPanel, "Settings");

        add(contentPanel, BorderLayout.CENTER);

        // Shutdown hook to gracefully stop threads
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MonitoringService.stopMonitoring();
            }
        });
    }

    private void makePanelTransparent(JPanel panel) {
        panel.setOpaque(false);
    }

    private JPanel createSidebar() {
        // Gradient Sidebar: Midnight Indigo (#0D101D) -> Dark Slate (#070912)
        Color sidebarStart = new Color(13, 16, 29);
        Color sidebarEnd = new Color(7, 9, 18);
        
        GradientPanel sidebar = new GradientPanel(sidebarStart, sidebarEnd, new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, getHeight()));

        // Logo Header
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("⚡ PULSE");
        logoLabel.setForeground(new Color(0, 245, 160)); // Neon Green
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Navigation Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        dashboardBtn = createNavButton("📊 Dashboard", true);
        endpointsBtn = createNavButton("🔗 API Endpoints", false);
        historyBtn = createNavButton("📜 Monitoring History", false);
        analyticsBtn = createNavButton("📈 Analytics", false);
        settingsBtn = createNavButton("⚙️ Settings", false);

        // Navigation Actions
        dashboardBtn.addActionListener(e -> switchPage("Dashboard", dashboardBtn));
        endpointsBtn.addActionListener(e -> switchPage("Endpoints", endpointsBtn));
        historyBtn.addActionListener(e -> switchPage("History", historyBtn));
        analyticsBtn.addActionListener(e -> switchPage("Analytics", analyticsBtn));
        settingsBtn.addActionListener(e -> switchPage("Settings", settingsBtn));

        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(endpointsBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(historyBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(analyticsBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(settingsBtn);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Footer status
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        footerPanel.setOpaque(false);
        JLabel statusLabel = new JLabel("● System Online (v1.0)");
        statusLabel.setForeground(new Color(0, 245, 160));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footerPanel.add(statusLabel);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void switchPage(String pageName, JButton activeBtn) {
        cardLayout.show(contentPanel, pageName);
        
        // Reset all button highlights
        dashboardBtn.setForeground(new Color(146, 155, 176));
        dashboardBtn.setBackground(new Color(14, 17, 27, 120));
        endpointsBtn.setForeground(new Color(146, 155, 176));
        endpointsBtn.setBackground(new Color(14, 17, 27, 120));
        historyBtn.setForeground(new Color(146, 155, 176));
        historyBtn.setBackground(new Color(14, 17, 27, 120));
        analyticsBtn.setForeground(new Color(146, 155, 176));
        analyticsBtn.setBackground(new Color(14, 17, 27, 120));
        settingsBtn.setForeground(new Color(146, 155, 176));
        settingsBtn.setBackground(new Color(14, 17, 27, 120));

        // Highlight selected button with subtle translucency
        activeBtn.setForeground(new Color(245, 247, 255));
        activeBtn.setBackground(new Color(30, 40, 65));
    }

    private JButton createNavButton(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(selected ? new Color(245, 247, 255) : new Color(146, 155, 176));
        btn.setBackground(selected ? new Color(30, 40, 65) : new Color(14, 17, 27, 120));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        UIManager.put("Panel.background", new Color(9, 11, 18));
        
        SwingUtilities.invokeLater(() -> {
            new PulseApp().setVisible(true);
        });
    }
}