package services.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.LogEntry;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class log_historyService {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
    }

    public ObservableList<LogEntry> getAllLogs() throws Exception {
        ObservableList<LogEntry> logs = FXCollections.observableArrayList();
        String query = "SELECT * FROM logentry ORDER BY date DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                logs.add(new LogEntry(
                        rs.getString("date"),
                        rs.getString("user"),
                        rs.getString("action"),
                        rs.getString("details")
                ));
            }
        }
        return logs;
    }

    public ObservableList<LogEntry> getFilteredLogs(LocalDate startDate, LocalDate endDate, String action) throws Exception {
        ObservableList<LogEntry> logs = FXCollections.observableArrayList();
        List<Object> params = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT * FROM logentry WHERE 1=1");

        if (startDate != null && endDate != null) {
            query.append(" AND DATE(date) BETWEEN ? AND ?");
            params.add(startDate);
            params.add(endDate);
        } else if (startDate != null) {
            query.append(" AND DATE(date) >= ?");
            params.add(startDate);
        } else if (endDate != null) {
            query.append(" AND DATE(date) <= ?");
            params.add(endDate);
        }

        if (action != null && !action.isEmpty() && !action.equals("All")) {
            query.append(" AND action = ?");
            params.add(action);
        }

        query.append(" ORDER BY date DESC");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof LocalDate) {
                    ps.setDate(i + 1, Date.valueOf((LocalDate) params.get(i)));
                } else {
                    ps.setString(i + 1, params.get(i).toString());
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new LogEntry(
                            rs.getString("date"),
                            rs.getString("user"),
                            rs.getString("action"),
                            rs.getString("details")
                    ));
                }
            }
        }
        return logs;
    }

    public void addLog(String user, String action, String details) throws Exception {
        String query = "INSERT INTO logentry (date, user, action, details) VALUES (NOW(), ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, user);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        }
    }
}