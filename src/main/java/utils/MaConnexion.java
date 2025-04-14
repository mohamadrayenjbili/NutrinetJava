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
        return connection;
    }
}