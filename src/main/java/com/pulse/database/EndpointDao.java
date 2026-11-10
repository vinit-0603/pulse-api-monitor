package com.pulse.database;

import com.pulse.model.Endpoint;
import com.pulse.model.HistoryRecord;
// import com.pulse.model.Incident;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EndpointDao {

    public static List<Endpoint> getAllEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        String sql = "SELECT * FROM endpoints ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                endpoints.add(mapResultSetToEndpoint(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching endpoints: " + e.getMessage());
        }
        return endpoints;
    }

    public static void saveEndpoint(Endpoint endpoint) {
    String sql = "INSERT INTO endpoints (name, url, method, interval_seconds, timeout_seconds, expected_status_codes, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setString(1, endpoint.getName());
        pstmt.setString(2, endpoint.getUrl());
        pstmt.setString(3, endpoint.getMethod());
        pstmt.setInt(4, endpoint.getIntervalSeconds());
        pstmt.setInt(5, endpoint.getTimeoutSeconds());
        pstmt.setString(6, endpoint.getExpectedStatusCodes());
        pstmt.setInt(7, endpoint.isActive() ? 1 : 0);
        pstmt.setString(8, LocalDateTime.now().toString());
        pstmt.executeUpdate();

        // Read back the real database ID so history is saved under it
        try (ResultSet keys = pstmt.getGeneratedKeys()) {
            if (keys.next()) {
                endpoint.setId(keys.getInt(1));
            }
        }
    } catch (SQLException e) {
        System.err.println("Error saving endpoint: " + e.getMessage());
    }
}

    public static void updateEndpoint(Endpoint endpoint) {
        String sql = "UPDATE endpoints SET name=?, url=?, method=?, interval_seconds=?, timeout_seconds=?, expected_status_codes=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, endpoint.getName());
            pstmt.setString(2, endpoint.getUrl());
            pstmt.setString(3, endpoint.getMethod());
            pstmt.setInt(4, endpoint.getIntervalSeconds());
            pstmt.setInt(5, endpoint.getTimeoutSeconds());
            pstmt.setString(6, endpoint.getExpectedStatusCodes());
            pstmt.setInt(7, endpoint.isActive() ? 1 : 0);
            pstmt.setInt(8, endpoint.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating endpoint: " + e.getMessage());
        }
    }

    public static void deleteEndpoint(int id) {
        String sql = "DELETE FROM endpoints WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting endpoint: " + e.getMessage());
        }
    }

    public static void saveHistoryRecord(HistoryRecord record) {
        String sql = "INSERT INTO monitoring_history (endpoint_id, checked_at, response_time_ms, http_status_code, status, error_message) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, record.getEndpointId());
            pstmt.setString(2, record.getCheckedAt().toString());
            pstmt.setLong(3, record.getResponseTimeMs());
            pstmt.setInt(4, record.getHttpStatusCode());
            pstmt.setString(5, record.getStatus());
            pstmt.setString(6, record.getErrorMessage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving history record: " + e.getMessage());
        }
    }

    public static List<HistoryRecord> getHistoryForEndpoint(int endpointId, int limit) {
        List<HistoryRecord> history = new ArrayList<>();
        String sql = "SELECT * FROM monitoring_history WHERE endpoint_id = ? ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, endpointId);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new HistoryRecord(
                        rs.getInt("id"),
                        rs.getInt("endpoint_id"),
                        LocalDateTime.parse(rs.getString("checked_at")),
                        rs.getString("status"),
                        rs.getInt("http_status_code"),
                        rs.getLong("response_time_ms"),
                        rs.getString("error_message")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching history: " + e.getMessage());
        }
        return history;
    }

    private static Endpoint mapResultSetToEndpoint(ResultSet rs) throws SQLException {
        return new Endpoint(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("url"),
                rs.getString("method"),
                rs.getInt("interval_seconds"),
                rs.getInt("timeout_seconds"),
                rs.getString("expected_status_codes"),
                rs.getInt("is_active") == 1,
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }
}