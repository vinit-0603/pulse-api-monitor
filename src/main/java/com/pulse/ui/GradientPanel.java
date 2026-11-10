package com.pulse.ui;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    private Color color1;
    private Color color2;

    public GradientPanel(Color color1, Color color2, LayoutManager layout) {
        super(layout);
        this.color1 = color1;
        this.color2 = color2;
    }

    public GradientPanel(Color color1, Color color2) {
        this(color1, color2, new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create diagonal dark gradient effect
        GradientPaint gp = new GradientPaint(
                0, 0, color1, 
                getWidth(), getHeight(), color2
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
