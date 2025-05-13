package controllers;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.WindowUtils;

public class Home extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            // Charger la scène de connexion
            WindowUtils.changeScene(stage, "/user/sign_in.fxml", "Accueil");
            
            // Ajouter l'icône
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_logo.png")));
            
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
        }
    }
}
