package services.Programme;


import models.Programme;
import services.Programme.IProgrammeService;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeService implements IProgrammeService {

    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    @Override
    public void ajouterProgramme(Programme programme) throws SQLException {
        String query = "INSERT INTO programmes (titre, type, video_url, image, auteur, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, programme.getTitre());
            pstmt.setString(2, programme.getType());
            pstmt.setString(3, programme.getVideoUrl());
            pstmt.setString(4, programme.getImage());
            pstmt.setString(5, programme.getAuteur());
            pstmt.setString(6, programme.getDescription());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    programme.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Programme> getAllProgrammes() throws SQLException {
        List<Programme> programmes = new ArrayList<>();
        String query = "SELECT * FROM programmes";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Programme p = new Programme();
                p.setId(rs.getInt("id"));
                p.setTitre(rs.getString("titre"));
                p.setType(rs.getString("type"));
                p.setVideoUrl(rs.getString("video_url"));
                p.setImage(rs.getString("image"));
                p.setAuteur(rs.getString("auteur"));
                p.setDescription(rs.getString("description"));

                programmes.add(p);
            }
        }
        return programmes;
    }

    @Override
    public Programme getProgrammeById(int id) throws SQLException {
        String query = "SELECT * FROM programmes WHERE id = ?";
        Programme programme = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    programme = new Programme();
                    programme.setId(rs.getInt("id"));
                    programme.setTitre(rs.getString("titre"));
                    programme.setType(rs.getString("type"));
                    programme.setVideoUrl(rs.getString("video_url"));
                    programme.setImage(rs.getString("image"));
                    programme.setAuteur(rs.getString("auteur"));
                    programme.setDescription(rs.getString("description"));
                }
            }
        }
        return programme;
    }

    @Override
    public void updateProgramme(Programme programme) throws SQLException {
        String query = "UPDATE programmes SET titre = ?, type = ?, video_url = ?, image = ?, auteur = ?, description = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, programme.getTitre());
            pstmt.setString(2, programme.getType());
            pstmt.setString(3, programme.getVideoUrl());
            pstmt.setString(4, programme.getImage());
            pstmt.setString(5, programme.getAuteur());
            pstmt.setString(6, programme.getDescription());
            pstmt.setInt(7, programme.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteProgramme(int id) throws SQLException {
        String query = "DELETE FROM programmes WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}

