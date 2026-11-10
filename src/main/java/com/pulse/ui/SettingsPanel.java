package com.pulse.ui;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(9, 11, 18));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
        JLabel titleLabel = new JLabel("System Settings & Preferences");
        titleLabel.setForeground(new Color(245, 247, 255));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Form Settings Container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(20, 25, 39));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 31, 48), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JCheckBox desktopNotifications = new JCheckBox(" Enable Desktop Failure Alerts");
        desktopNotifications.setForeground(new Color(245, 247, 255));
        desktopNotifications.setBackground(new Color(20, 25, 39));
        desktopNotifications.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desktopNotifications.setSelected(true);

        JCheckBox darkThemeCheck = new JCheckBox(" Force Dark SaaS Theme (FlatLaf)");
        darkThemeCheck.setForeground(new Color(245, 247, 255));
        darkThemeCheck.setBackground(new Color(20, 25, 39));
        darkThemeCheck.setFont(new Font("SansSerif", Font.PLAIN, 14));
        darkThemeCheck.setSelected(true);

        JButton saveBtn = new JButton("Save Preferences");
        saveBtn.setBackground(new Color(0, 245, 160));
        saveBtn.setForeground(new Color(9, 11, 18));
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Preferences saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE));

        formPanel.add(desktopNotifications);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(darkThemeCheck);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(saveBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.NORTH);

        add(wrapper, BorderLayout.CENTER);
    }
}