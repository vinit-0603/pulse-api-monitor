package com.pulse.ui;

import com.pulse.database.EndpointDao;
import com.pulse.model.Endpoint;
import com.pulse.model.HistoryRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JLabel totalApisLabel;
    private JLabel onlineApisLabel;
    private JLabel failedApisLabel;
    private JLabel uptimeLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(9, 11, 18));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Top Header
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setForeground(new Color(245, 247, 255));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Center Content Area
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // Stat Cards Container
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(getWidth(), 120));

        totalApisLabel = new JLabel("0", SwingConstants.CENTER);
        onlineApisLabel = new JLabel("0", SwingConstants.CENTER);
        failedApisLabel = new JLabel("0", SwingConstants.CENTER);
        uptimeLabel = new JLabel("100%", SwingConstants.CENTER);

        statsPanel.add(createCard("Total APIs", totalApisLabel, new Color(167, 139, 250)));
        statsPanel.add(createCard("Online APIs", onlineApisLabel, new Color(0, 245, 160)));
        statsPanel.add(createCard("Failed APIs", failedApisLabel, new Color(255, 75, 75)));
        statsPanel.add(createCard("Overall Uptime", uptimeLabel, new Color(56, 189, 248)));

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"API Name", "URL", "Method", "Interval", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setBackground(new Color(20, 25, 39));
        table.setForeground(new Color(245, 247, 255));
        table.getTableHeader().setBackground(new Color(25, 31, 48));
        table.getTableHeader().setForeground(new Color(146, 155, 176));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        // Color-code the Status column
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(20, 25, 39));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(25, 31, 48)));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Auto-refresh stats every 5 seconds
        Timer timer = new Timer(5000, e -> refreshData());
        timer.start();

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Endpoint> endpoints = EndpointDao.getAllEndpoints();

        int total = endpoints.size();
        int online = 0;
        int failed = 0;
        int paused = 0;

        for (Endpoint ep : endpoints) {
            String currentStatus;

            if (!ep.isActive()) {
                // Paused endpoints are shown as PAUSED and not counted as online/failed
                currentStatus = "PAUSED";
                paused++;
            } else {
                currentStatus = "ACTIVE"; // active, but no check has finished yet

                List<HistoryRecord> history = EndpointDao.getHistoryForEndpoint(ep.getId(), 1);
                if (!history.isEmpty()) {
                    HistoryRecord lastCheck = history.get(0);
                    if ("SUCCESS".equalsIgnoreCase(lastCheck.getStatus())) {
                        currentStatus = "ONLINE";
                        online++;
                    } else {
                        currentStatus = "FAILED";
                        failed++;
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    ep.getName(),
                    ep.getUrl(),
                    ep.getMethod(),
                    ep.getIntervalSeconds() + "s",
                    currentStatus,
                    "Manage"
            });
        }

        totalApisLabel.setText(String.valueOf(total));
        onlineApisLabel.setText(String.valueOf(online));
        failedApisLabel.setText(String.valueOf(failed));

        // Uptime is based only on endpoints that are being monitored (not paused)
        int monitored = total - paused;
        double uptime = monitored > 0 ? ((double) online / monitored) * 100 : 100.0;
        uptimeLabel.setText(String.format("%.0f%%", uptime));
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(20, 25, 39));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 31, 48), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(146, 155, 176));
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /** Colors the Status column text: green, red, yellow, or grey. */
    private static class StatusCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value == null ? "" : value.toString();
            switch (status) {
                case "ONLINE" -> c.setForeground(new Color(0, 245, 160));
                case "FAILED" -> c.setForeground(new Color(255, 75, 75));
                case "PAUSED" -> c.setForeground(new Color(250, 204, 21));
                default -> c.setForeground(new Color(146, 155, 176));
            }
            return c;
        }
    }
}