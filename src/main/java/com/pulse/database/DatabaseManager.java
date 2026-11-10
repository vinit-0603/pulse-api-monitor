package com.pulse.database;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".pulse";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + File.separator + "pulse.db";

    public static void initializeDatabase() {
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Create Endpoints table
            String sqlEndpoints = "CREATE TABLE IF NOT EXISTS endpoints (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "url TEXT NOT NULL, " +
                    "method TEXT NOT NULL DEFAULT 'GET', " +
                    "interval_seconds INTEGER NOT NULL DEFAULT 60, " +
                    "timeout_seconds INTEGER NOT NULL DEFAULT 10, " +
                    "expected_status_codes TEXT NOT NULL DEFAULT '200', " +
                    "is_active INTEGER NOT NULL DEFAULT 1, " +
                    "created_at TEXT NOT NULL" +
                    ");";
            stmt.execute(sqlEndpoints);

            // Create Monitoring History table
            String sqlHistory = "CREATE TABLE IF NOT EXISTS monitoring_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "endpoint_id INTEGER NOT NULL, " +
                    "checked_at TEXT NOT NULL, " +
                    "response_time_ms INTEGER NOT NULL, " +
                    "http_status_code INTEGER NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "error_message TEXT, " +
                    "FOREIGN KEY(endpoint_id) REFERENCES endpoints(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(sqlHistory);

            // Create Incidents table
            String sqlIncidents = "CREATE TABLE IF NOT EXISTS incidents (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "endpoint_id INTEGER NOT NULL, " +
                    "started_at TEXT NOT NULL, " +
                    "recovered_at TEXT, " +
                    "failure_reason TEXT, " +
                    "downtime_seconds INTEGER DEFAULT 0, " +
                    "status TEXT NOT NULL, " +
                    "FOREIGN KEY(endpoint_id) REFERENCES endpoints(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(sqlIncidents);

            // Insert sample endpoints if table is empty
            seedDefaultEndpoints(conn);

            System.out.println("SQLite Database initialized successfully at: " + DB_URL);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static void seedDefaultEndpoints(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM endpoints";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO endpoints (name, url, method, interval_seconds, timeout_seconds, expected_status_codes, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    // Sample 1: httpbin get
                    pstmt.setString(1, "HTTPBin Get API");
                    pstmt.setString(2, "https://httpbin.org/get");
                    pstmt.setString(3, "GET");
                    pstmt.setInt(4, 30);
                    pstmt.setInt(5, 10);
                    pstmt.setString(6, "200");
                    pstmt.setInt(7, 1);
                    pstmt.setString(8, LocalDateTime.now().toString());
                    pstmt.executeUpdate();

                    // Sample 2: JSONPlaceholder Posts
                    pstmt.setString(1, "JSONPlaceholder Post #1");
                    pstmt.setString(2, "https://jsonplaceholder.typicode.com/posts/1");
                    pstmt.setString(3, "GET");
                    pstmt.setInt(4, 45);
                    pstmt.setInt(5, 10);
                    pstmt.setString(6, "200");
                    pstmt.setInt(7, 1);
                    pstmt.setString(8, LocalDateTime.now().toString());
                    pstmt.executeUpdate();

                    // Sample 3: HTTPBin Status 500 (Demo Error State)
                    pstmt.setString(1, "HTTPBin Error Tester (500)");
                    pstmt.setString(2, "https://httpbin.org/status/500");
                    pstmt.setString(3, "GET");
                    pstmt.setInt(4, 60);
                    pstmt.setInt(5, 10);
                    pstmt.setString(6, "200"); 
                    pstmt.setInt(7, 0); 
                    pstmt.setString(8, LocalDateTime.now().toString());
                    pstmt.executeUpdate();
                }
                System.out.println("Sample demonstration endpoints seeded successfully.");
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}