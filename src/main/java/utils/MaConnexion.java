package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnexion {
    private static final String URL = "jdbc:mysql://localhost:3306/didou";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static MaConnexion instance;
    private Connection connection;

    private MaConnexion() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à la base de données réussie!");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    public static synchronized MaConnexion getInstance() {
        if (instance == null) {
            instance = new MaConnexion();
        }
        return instance;
    }


    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔁 Reconnexion à la base de données...");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
            }
        } catch (SQLException e) {
            System.err.println("❌ Impossible de récupérer la connexion : " + e.getMessage());
        }
        return connection;
    }
}
