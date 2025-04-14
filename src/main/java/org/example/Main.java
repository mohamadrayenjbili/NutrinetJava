package org.example;

import utils.MaConnexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;

public class Main extends Application {

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Establish a database connection
            Connection conn = MaConnexion.getInstance().getConnection();
            System.out.println("Database connection established: " + conn);

            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("sign_up.fxml"));

            // Set the title of the window
            primaryStage.setTitle("User Signup");

            // Create a Scene with the FXML root
            Scene scene = new Scene(root);

            // Set the Scene for the Stage
            primaryStage.setScene(scene);

            // Show the Stage
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}