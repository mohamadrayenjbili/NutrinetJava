package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnexion {
    private static final String URL = "jdbc:mysql://localhost:3306/user";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static MaConnexion instance;
    private Connection connection;

    private MaConnexion() throws SQLException {
        try {
            // Chargement du driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Création de la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // Configuration supplémentaire
            connection.setAutoCommit(true); // Mode auto-commit activé
            System.out.println("Connexion à la base de données réussie!");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC MySQL non trouvé", e);
        } catch (SQLException e) {
            throw new SQLException("Échec de la connexion à la base de données", e);
        }
    }

    public static synchronized MaConnexion getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new MaConnexion();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        // Vérifie si la connexion est toujours valide
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
                System.out.println("Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
        instance = null;
    }
}