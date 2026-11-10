package com.pulse.ui;

import com.pulse.database.EndpointDao;
import com.pulse.model.Endpoint;
import com.pulse.model.HistoryRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;

    public HistoryPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(9, 11, 18));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
        JLabel titleLabel = new JLabel("Monitoring History & Audit Logs");
        titleLabel.setForeground(new Color(245, 247, 255));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Record ID", "Endpoint ID", "Checked At", "Status", "HTTP Code", "Response Time", "Error Message"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(35);
        historyTable.setBackground(new Color(20, 25, 39));
        historyTable.setForeground(new Color(245, 247, 255));
        historyTable.getTableHeader().setBackground(new Color(25, 31, 48));
        historyTable.getTableHeader().setForeground(new Color(146, 155, 176));
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(new Color(20, 25, 39));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(25, 31, 48)));

        add(scrollPane, BorderLayout.CENTER);
        loadHistoryData();
    }

    public void loadHistoryData() {
        tableModel.setRowCount(0);
        List<Endpoint> endpoints = EndpointDao.getAllEndpoints();
        for (Endpoint ep : endpoints) {
            List<HistoryRecord> historyList = EndpointDao.getHistoryForEndpoint(ep.getId(), 20);
            for (HistoryRecord hr : historyList) {
                tableModel.addRow(new Object[]{
                        hr.getId(),
                        hr.getEndpointId(),
                        hr.getCheckedAt(),
                        hr.getStatus(),
                        hr.getHttpStatusCode(),
                        hr.getResponseTimeMs() + "ms",
                        hr.getErrorMessage() != null ? hr.getErrorMessage() : "-"
                });
            }
        }
    }
}