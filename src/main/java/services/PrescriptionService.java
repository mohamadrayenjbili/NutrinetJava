package Services;

import Models.Prescription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {
    private Connection connection;

    public PrescriptionService(Connection connection) {
        this.connection = connection;
    }

    public boolean addPrescription(Prescription prescription) throws SQLException {
        String query = "INSERT INTO prescription (consultation_id, nom_p, prenom_p, date, objectif, programme) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, prescription.getConsultationId());
            statement.setString(2, prescription.getNomP());
            statement.setString(3, prescription.getPrenomP());
            statement.setDate(4, new java.sql.Date(prescription.getDate().getTime()));
            statement.setString(5, prescription.getObjectif());
            statement.setString(6, prescription.getProgramme());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean updatePrescription(Prescription prescription) throws SQLException {
        String query = "UPDATE prescription SET date = ?, objectif = ?, programme = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, new java.sql.Date(prescription.getDate().getTime()));
            statement.setString(2, prescription.getObjectif());
            statement.setString(3, prescription.getProgramme());
            statement.setInt(4, prescription.getId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePrescription(int id) throws SQLException {
        String query = "DELETE FROM prescription WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public Prescription getPrescriptionByConsultationId(int consultationId) throws SQLException {
        String query = "SELECT * FROM prescription WHERE consultation_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, consultationId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Prescription(
                        resultSet.getInt("id"),
                        resultSet.getInt("consultation_id"),
                        resultSet.getString("nom_p"),
                        resultSet.getString("prenom_p"),
                        resultSet.getDate("date"),
                        resultSet.getString("objectif"),
                        resultSet.getString("programme")
                );
            }
            return null;
        }
    }
}