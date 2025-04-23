package services;



import models.Consultation;
import javafx.beans.Observable;
import javafx.util.Callback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService implements IServiceConsultation {
    private Connection connection;

    public ConsultationService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addConsultation(Consultation consultation) throws SQLException {
        String query = "INSERT INTO consultation (nom, prenom, tel, mail, type, date, note, user_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, consultation.getNom());
            statement.setString(2, consultation.getPrenom());
            statement.setInt(3, consultation.getTel());
            statement.setString(4, consultation.getMail());
            statement.setString(5, consultation.getType());
            statement.setTimestamp(6, Timestamp.valueOf(consultation.getDate()));
            statement.setString(7, consultation.getNote());
            if (consultation.getUserId() != null) {
                statement.setInt(8, consultation.getUserId());
            } else {
                statement.setNull(8, Types.INTEGER);
            }
            statement.setString(9, consultation.getStatus());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consultation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Consultation> getAllConsultations() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Consultation consultation = new Consultation();
                consultation.setId(resultSet.getInt("id"));
                consultation.setNom(resultSet.getString("nom"));
                consultation.setPrenom(resultSet.getString("prenom"));
                consultation.setTel(resultSet.getInt("tel"));
                consultation.setMail(resultSet.getString("mail"));
                consultation.setType(resultSet.getString("type"));

                Timestamp timestamp = resultSet.getTimestamp("date");
                if (timestamp != null) {
                    consultation.setDate(timestamp.toLocalDateTime());
                }

                consultation.setNote(resultSet.getString("note"));
                int userId = resultSet.getInt("user_id");
                consultation.setUserId(resultSet.wasNull() ? null : userId);
                consultation.setStatus(resultSet.getString("status"));

                consultations.add(consultation);
            }
        }

        return consultations;
    }

    @Override
    public Consultation getConsultationById(int id) throws SQLException {
        String query = "SELECT * FROM consultation WHERE id = ?";
        Consultation consultation = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    consultation = new Consultation();
                    consultation.setId(resultSet.getInt("id"));
                    consultation.setNom(resultSet.getString("nom"));
                    consultation.setPrenom(resultSet.getString("prenom"));
                    consultation.setTel(resultSet.getInt("tel"));
                    consultation.setMail(resultSet.getString("mail"));
                    consultation.setType(resultSet.getString("type"));

                    Timestamp timestamp = resultSet.getTimestamp("date");
                    if (timestamp != null) {
                        consultation.setDate(timestamp.toLocalDateTime());
                    }

                    consultation.setNote(resultSet.getString("note"));
                    int userId = resultSet.getInt("user_id");
                    consultation.setUserId(resultSet.wasNull() ? null : userId);
                    consultation.setStatus(resultSet.getString("status"));
                }
            }
        }

        return consultation;
    }

    @Override
    public void updateConsultation(Consultation consultation) throws SQLException {
        String query = "UPDATE consultation SET nom = ?, prenom = ?, tel = ?, mail = ?, type = ?, " +
                "date = ?, note = ?, user_id = ?, status = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, consultation.getNom());
            statement.setString(2, consultation.getPrenom());
            statement.setInt(3, consultation.getTel());
            statement.setString(4, consultation.getMail());
            statement.setString(5, consultation.getType());
            statement.setTimestamp(6, Timestamp.valueOf(consultation.getDate()));
            statement.setString(7, consultation.getNote());
            if (consultation.getUserId() != null) {
                statement.setInt(8, consultation.getUserId());
            } else {
                statement.setNull(8, Types.INTEGER);
            }
            statement.setString(9, consultation.getStatus());
            statement.setInt(10, consultation.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public boolean deleteConsultation(int id) throws SQLException {
        // Désactiver temporairement les contraintes de clé étrangère si nécessaire
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        }

        String query = "DELETE FROM consultation WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            // Réactiver les contraintes
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            // Réactiver les contraintes en cas d'erreur
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
            }
            throw e;
        }
    }

    @Override
    public List<Consultation> getConsultationsByStatus(String status) throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation WHERE status = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Consultation consultation = new Consultation();
                    consultation.setId(resultSet.getInt("id"));
                    consultation.setNom(resultSet.getString("nom"));
                    consultation.setPrenom(resultSet.getString("prenom"));
                    consultation.setTel(resultSet.getInt("tel"));
                    consultation.setMail(resultSet.getString("mail"));
                    consultation.setType(resultSet.getString("type"));

                    Timestamp timestamp = resultSet.getTimestamp("date");
                    if (timestamp != null) {
                        consultation.setDate(timestamp.toLocalDateTime());
                    }

                    consultation.setNote(resultSet.getString("note"));
                    int userId = resultSet.getInt("user_id");
                    consultation.setUserId(resultSet.wasNull() ? null : userId);
                    consultation.setStatus(resultSet.getString("status"));

                    consultations.add(consultation);
                }
            }
        }

        return consultations;
    }
    public boolean hasPrescription(int consultationId) throws SQLException {
        String query = "SELECT COUNT(*) FROM prescription WHERE consultation_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, consultationId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Si une prescription existe
            }
        }
        return false;
    }

    // Méthode pour récupérer les détails de la consultation (par exemple, le nom et prénom du patient)
    public String[] getConsultationDetails(int consultationId) {
        String query = "SELECT nom_patient, prenom_patient FROM consultation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, consultationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("nom_patient");
                String prenom = rs.getString("prenom_patient");
                return new String[]{nom, prenom};  // Renvoie un tableau de chaînes contenant le nom et le prénom
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

