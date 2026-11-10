package com.pulse.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class AnalyticsPanel extends JPanel {

    public AnalyticsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(9, 11, 18));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title Header
        JLabel titleLabel = new JLabel("API Performance & Response Time Trends");
        titleLabel.setForeground(new Color(245, 247, 255));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Create Chart Dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Sample data points to showcase chart rendering cleanly
        dataset.addValue(120, "HTTPBin Get API", "12:00");
        dataset.addValue(95, "HTTPBin Get API", "12:05");
        dataset.addValue(110, "HTTPBin Get API", "12:10");
        dataset.addValue(140, "JSONPlaceholder Post #1", "12:00");
        dataset.addValue(105, "JSONPlaceholder Post #1", "12:05");
        dataset.addValue(115, "JSONPlaceholder Post #1", "12:10");

        // Build JFreeChart Line Chart
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Response Time (ms) over Time",
                "Time Check",
                "Response Time (ms)",
                dataset
        );

        // Customize Chart Styling for Dark SaaS Theme
        lineChart.setBackgroundPaint(new Color(20, 25, 39));
        lineChart.getTitle().setPaint(new Color(245, 247, 255));

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(25, 31, 48));
        plot.setDomainGridlinePaint(new Color(40, 50, 75));
        plot.setRangeGridlinePaint(new Color(40, 50, 75));

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(0, 245, 160)); // Neon Green
        renderer.setSeriesPaint(1, new Color(167, 139, 250)); // Electric Purple
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setBackground(new Color(20, 25, 39));
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(25, 31, 48)));

        add(chartPanel, BorderLayout.CENTER);
    }
}