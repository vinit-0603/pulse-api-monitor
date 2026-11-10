package com.pulse.ui;

import com.pulse.database.EndpointDao;
import com.pulse.model.Endpoint;
import com.pulse.service.MonitoringService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class EndpointsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton editBtn, pauseBtn, deleteBtn;

    public EndpointsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(9, 11, 18));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // ---------- Header ----------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("API Endpoints Management");
        titleLabel.setForeground(new Color(245, 247, 255));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton addBtn = createButton("+ Add Endpoint", new Color(0, 245, 160), new Color(9, 11, 18));
        addBtn.addActionListener(e -> showEndpointDialog(null));
        headerPanel.add(addBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ---------- Table ----------
        String[] columns = {"ID", "Name", "URL", "Method", "Interval", "Timeout", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(35);
        table.setBackground(new Color(20, 25, 39));
        table.setForeground(new Color(245, 247, 255));
        table.getTableHeader().setBackground(new Color(25, 31, 48));
        table.getTableHeader().setForeground(new Color(146, 155, 176));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(20, 25, 39));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(25, 31, 48)));
        add(scrollPane, BorderLayout.CENTER);

        // ---------- Action bar (Edit / Pause / Delete) ----------
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBar.setOpaque(false);

        editBtn = createButton("Edit", new Color(56, 189, 248), new Color(9, 11, 18));
        pauseBtn = createButton("Pause", new Color(250, 204, 21), new Color(9, 11, 18));
        deleteBtn = createButton("Delete", new Color(255, 75, 75), new Color(255, 255, 255));

        editBtn.addActionListener(e -> onEdit());
        pauseBtn.addActionListener(e -> onTogglePause());
        deleteBtn.addActionListener(e -> onDelete());

        actionBar.add(editBtn);
        actionBar.add(pauseBtn);
        actionBar.add(deleteBtn);
        add(actionBar, BorderLayout.SOUTH);

        loadTableData();
        updateButtonStates();
    }

    // =====================================================================
    // Data loading
    // =====================================================================

    public void loadTableData() {
        tableModel.setRowCount(0);
        List<Endpoint> endpoints = EndpointDao.getAllEndpoints();
        for (Endpoint ep : endpoints) {
            tableModel.addRow(new Object[]{
                    ep.getId(),
                    ep.getName(),
                    ep.getUrl(),
                    ep.getMethod(),
                    ep.getIntervalSeconds() + "s",
                    ep.getTimeoutSeconds() + "s",
                    ep.isActive() ? "ACTIVE" : "PAUSED"
            });
        }
        updateButtonStates();
    }

    private Endpoint getSelectedEndpoint() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);
        for (Endpoint ep : EndpointDao.getAllEndpoints()) {
            if (ep.getId() == id) return ep;
        }
        return null;
    }

    private void updateButtonStates() {
        boolean hasSelection = table.getSelectedRow() >= 0;
        editBtn.setEnabled(hasSelection);
        pauseBtn.setEnabled(hasSelection);
        deleteBtn.setEnabled(hasSelection);

        if (hasSelection) {
            int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
            boolean paused = "PAUSED".equals(tableModel.getValueAt(modelRow, 6));
            pauseBtn.setText(paused ? "Resume" : "Pause");
        } else {
            pauseBtn.setText("Pause");
        }
    }

    // =====================================================================
    // Actions
    // =====================================================================

    private void onEdit() {
        Endpoint ep = getSelectedEndpoint();
        if (ep != null) showEndpointDialog(ep);
    }

    private void onTogglePause() {
        Endpoint ep = getSelectedEndpoint();
        if (ep == null) return;

        ep.setActive(!ep.isActive());
        EndpointDao.updateEndpoint(ep);
        // Cancels the running task when paused, starts a new one when resumed
        MonitoringService.scheduleEndpoint(ep);
        loadTableData();
    }

    private void onDelete() {
        Endpoint ep = getSelectedEndpoint();
        if (ep == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + ep.getName() + "\" and all its history?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        MonitoringService.cancelEndpoint(ep.getId()); // stop the task first
        EndpointDao.deleteEndpoint(ep.getId());
        loadTableData();
    }

    // =====================================================================
    // Add / Edit dialog (existing == null means "add")
    // =====================================================================

    private void showEndpointDialog(Endpoint existing) {
        boolean isEdit = existing != null;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit API Endpoint" : "Add New API Endpoint", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.getContentPane().setBackground(new Color(20, 25, 39));
        ((JComponent) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(isEdit ? existing.getName() : "");
        JTextField urlField = new JTextField(isEdit ? existing.getUrl() : "https://");
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"GET", "HEAD"});
        if (isEdit) methodCombo.setSelectedItem(existing.getMethod());
        JTextField intervalField = new JTextField(isEdit ? String.valueOf(existing.getIntervalSeconds()) : "30");
        JTextField timeoutField = new JTextField(isEdit ? String.valueOf(existing.getTimeoutSeconds()) : "10");
        JTextField codesField = new JTextField(isEdit ? existing.getExpectedStatusCodes() : "200");

        dialog.add(createLabel("API Name:"));
        dialog.add(nameField);
        dialog.add(createLabel("API URL:"));
        dialog.add(urlField);
        dialog.add(createLabel("HTTP Method:"));
        dialog.add(methodCombo);
        dialog.add(createLabel("Interval (seconds):"));
        dialog.add(intervalField);
        dialog.add(createLabel("Timeout (seconds):"));
        dialog.add(timeoutField);
        dialog.add(createLabel("Expected Status Codes:"));
        dialog.add(codesField);

        JButton saveBtn = createButton(isEdit ? "Update & Restart Monitor" : "Save & Monitor",
                new Color(0, 245, 160), new Color(9, 11, 18));

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String url = urlField.getText().trim();
            String codes = codesField.getText().trim();
            int interval, timeout;

            try {
                interval = Integer.parseInt(intervalField.getText().trim());
                timeout = Integer.parseInt(timeoutField.getText().trim());
            } catch (NumberFormatException ex) {
                showError(dialog, "Interval and timeout must be whole numbers.");
                return;
            }

            if (name.isEmpty() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                showError(dialog, "Please enter a valid name and an http/https URL.");
                return;
            }
            if (interval < 5) {
                showError(dialog, "Interval must be at least 5 seconds.");
                return;
            }
            if (timeout < 1) {
                showError(dialog, "Timeout must be at least 1 second.");
                return;
            }
            if (!codes.matches("\\d{3}(\\s*,\\s*\\d{3})*")) {
                showError(dialog, "Status codes must look like: 200 or 200,201");
                return;
            }

            String method = (String) methodCombo.getSelectedItem();

            if (isEdit) {
                existing.setName(name);
                existing.setUrl(url);
                existing.setMethod(method);
                existing.setIntervalSeconds(interval);
                existing.setTimeoutSeconds(timeout);
                existing.setExpectedStatusCodes(codes);
                EndpointDao.updateEndpoint(existing);
                MonitoringService.scheduleEndpoint(existing); // replaces the old task
            } else {
                Endpoint newEp = new Endpoint(0, name, url, method, interval, timeout,
                        codes, true, LocalDateTime.now());
                EndpointDao.saveEndpoint(newEp); // sets the real ID on newEp
                MonitoringService.scheduleEndpoint(newEp);
            }

            loadTableData();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(245, 247, 255));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return lbl;
    }
}