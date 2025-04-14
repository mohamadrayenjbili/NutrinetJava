package services;

import models.Objective;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectiveService implements IObjectiveService {

    private static Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    @Override
    public void ajouterObjective(Objective objective) throws SQLException {
        String query = "INSERT INTO objective (name, target_value, unit, start_date, end_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, objective.getName());
            pstmt.setDouble(2, objective.getTargetValue());
            pstmt.setString(3, objective.getUnit());
            pstmt.setDate(4, java.sql.Date.valueOf(objective.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(objective.getEndDate()));


            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    objective.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Objective> getAllObjectives() throws SQLException {
        List<Objective> objectives = new ArrayList<>();
        String query = "SELECT * FROM objective";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Objective o = new Objective();
                o.setId(rs.getInt("id"));
                o.setName(rs.getString("name"));
                o.setTargetValue(rs.getDouble("target_value"));
                o.setUnit(rs.getString("unit"));
                o.setStartDate(rs.getDate("start_date").toLocalDate()); // ✅
                o.setEndDate(rs.getDate("end_date").toLocalDate());     // ✅


                objectives.add(o);
            }
        }
        return objectives;
    }

    @Override
    public Objective getObjectiveByName(String name) throws SQLException {
        String query = "SELECT * FROM objective WHERE name = ?";
        Objective objective = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    objective = new Objective();
                    objective.setId(rs.getInt("id"));
                    objective.setName(rs.getString("name"));
                    objective.setTargetValue(rs.getDouble("target_value"));
                    objective.setUnit(rs.getString("unit"));
                    objective.setStartDate(rs.getDate("start_date").toLocalDate()); // ✅
                    objective.setEndDate(rs.getDate("end_date").toLocalDate());     // ✅

                }
            }
        }
        return objective;
    }

    @Override
    public void updateObjective(Objective objective) throws SQLException {
        String query = "UPDATE objective SET name = ?, target_value = ?, unit = ?, start_date = ?, end_date = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, objective.getName());
            pstmt.setDouble(2, objective.getTargetValue());
            pstmt.setString(3, objective.getUnit());
            pstmt.setDate(4, java.sql.Date.valueOf(objective.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(objective.getEndDate()));
            pstmt.setInt(6, objective.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteObjective(int id) throws SQLException {
        String query = "DELETE FROM objective WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }public static boolean isConnectionSuccessful() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection successful!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
        return false;
    }

}
