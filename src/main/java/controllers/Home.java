package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            // Charge le formulaire de consultation
            Parent root = FXMLLoader.load(getClass().getResource("/User/sign_in.fxml"));

            Scene scene = new Scene(root);
            stage.setTitle("Ajouter Consultation");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
        }
    }
}