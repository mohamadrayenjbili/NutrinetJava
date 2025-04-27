package services;

import models.Performance;
import utils.MaConnexion;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PerformanceService implements IPerformanceService {

    private static Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    @Override
    public void ajouterPerformance(Performance performance) throws SQLException {
        String query = "INSERT INTO performance (objective_id, date, notes, metric_name, value, unit) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, performance.getObjectiveId());
            pstmt.setTimestamp(2, Timestamp.valueOf(performance.getDate()));
            pstmt.setString(3, performance.getNotes());
            pstmt.setString(4, performance.getMetricName());
            pstmt.setDouble(5, performance.getValue());
            pstmt.setString(6, performance.getUnit());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    performance.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Performance> getAllPerformances() throws SQLException {
        List<Performance> performances = new ArrayList<>();
        String query = "SELECT * FROM performance";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Performance p = new Performance();
                p.setId(rs.getInt("id"));
                p.setObjectiveId(rs.getInt("objective_id"));
                p.setDate(rs.getTimestamp("date").toLocalDateTime());
                p.setNotes(rs.getString("notes"));
                p.setMetricName(rs.getString("metric_name"));
                p.setValue(rs.getDouble("value"));
                p.setUnit(rs.getString("unit"));

                performances.add(p);
            }
        }
        return performances;
    }

    @Override
    public List<Performance> getPerformancesByObjectiveId(int objectiveId) throws SQLException {
        List<Performance> performances = new ArrayList<>();
        String query = "SELECT * FROM performance WHERE objective_id = ? ORDER BY date DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, objectiveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Performance p = new Performance();
                    p.setId(rs.getInt("id"));
                    p.setObjectiveId(rs.getInt("objective_id"));
                    p.setDate(rs.getTimestamp("date").toLocalDateTime());
                    p.setNotes(rs.getString("notes"));
                    p.setMetricName(rs.getString("metric_name"));
                    p.setValue(rs.getDouble("value"));
                    p.setUnit(rs.getString("unit"));

                    performances.add(p);
                }
            }
        }
        return performances;
    }

    @Override
    public Performance getPerformanceById(int id) throws SQLException {
        String query = "SELECT * FROM performance WHERE id = ?";
        Performance performance = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    performance = new Performance();
                    performance.setId(rs.getInt("id"));
                    performance.setObjectiveId(rs.getInt("objective_id"));
                    performance.setDate(rs.getTimestamp("date").toLocalDateTime());
                    performance.setNotes(rs.getString("notes"));
                    performance.setMetricName(rs.getString("metric_name"));
                    performance.setValue(rs.getDouble("value"));
                    performance.setUnit(rs.getString("unit"));
                }
            }
        }
        return performance;
    }

    @Override
    public void updatePerformance(Performance performance) throws SQLException {
        String query = "UPDATE performance SET objective_id = ?, date = ?, notes = ?, metric_name = ?, value = ?, unit = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, performance.getObjectiveId());
            pstmt.setTimestamp(2, Timestamp.valueOf(performance.getDate()));
            pstmt.setString(3, performance.getNotes());
            pstmt.setString(4, performance.getMetricName());
            pstmt.setDouble(5, performance.getValue());
            pstmt.setString(6, performance.getUnit());
            pstmt.setInt(7, performance.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void deletePerformance(int id) throws SQLException {
        String query = "DELETE FROM performance WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<LocalDate, Double> getPerformanceTrend(int objectiveId) throws SQLException {
        Map<LocalDate, Double> trend = new TreeMap<>();
        String query = "SELECT DATE(date) as performance_date, AVG(value) as avg_value " +
                "FROM performance WHERE objective_id = ? " +
                "GROUP BY DATE(date) ORDER BY DATE(date)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, objectiveId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                trend.put(
                        rs.getDate("performance_date").toLocalDate(),
                        rs.getDouble("avg_value")
                );
            }
        }
        return trend;
    }
}